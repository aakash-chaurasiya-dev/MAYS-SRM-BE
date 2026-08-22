package com.mays.srm.inventory.service.impl;

import com.mays.srm.exception.BadRequestException;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.inventory.dto.request.PartsMasterLineRequestDTO;
import com.mays.srm.inventory.dto.request.PartsOrderOpenRequestDTO;
import com.mays.srm.inventory.dto.request.PartsOrderSaveRequestDTO;
import com.mays.srm.inventory.dto.request.PartsOrderStatusRequestDTO;
import com.mays.srm.inventory.dto.resDTO.PartsOrderModalResponseDTO;
import com.mays.srm.inventory.dto.resDTO.PartsOrderSummaryResponseDTO;
import com.mays.srm.inventory.entities.*;
import com.mays.srm.inventory.enums.PartsMasterSource;
import com.mays.srm.inventory.enums.PartsOrderStatus;
import com.mays.srm.inventory.enums.TicketPartStatus;
import com.mays.srm.inventory.repository.*;
import com.mays.srm.inventory.service.PartsOrderService;
import com.mays.srm.inventory.util.InventoryAuditHelper;
import com.mays.srm.ticket.entities.Ticket;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PartsOrderServiceImpl implements PartsOrderService {

    private final PartsOrderDao partsOrderDao;
    private final PartsMasterDao partsMasterDao;
    private final TicketPartDao ticketPartDao;
    private final InStockPartDao inStockPartDao;
    private final PartPriceDao partPriceDao;
    private final VendorDamagePartReturnDao vendorDamagePartReturnDao;
    private final StocksDao stocksDao;

    public PartsOrderServiceImpl(
            PartsOrderDao partsOrderDao,
            PartsMasterDao partsMasterDao,
            TicketPartDao ticketPartDao,
            InStockPartDao inStockPartDao,
            PartPriceDao partPriceDao,
            VendorDamagePartReturnDao vendorDamagePartReturnDao,
            StocksDao stocksDao) {
        this.partsOrderDao = partsOrderDao;
        this.partsMasterDao = partsMasterDao;
        this.ticketPartDao = ticketPartDao;
        this.inStockPartDao = inStockPartDao;
        this.partPriceDao = partPriceDao;
        this.vendorDamagePartReturnDao = vendorDamagePartReturnDao;
        this.stocksDao = stocksDao;
    }

    @Override
    public List<PartsOrderSummaryResponseDTO> getAll() {
        return partsOrderDao.findAllSummaries();
    }

    @Override
    public PartsOrderModalResponseDTO getById(Integer orderId) {
        return partsOrderDao.findModalByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
    }

    @Override
    public PartsOrderModalResponseDTO getByTicketPartId(Integer ticketPartId) {
        return partsOrderDao.findModalByTicketPartId(ticketPartId)
                .orElseThrow(() -> new ResourceNotFoundException("No order for ticket part: " + ticketPartId));
    }

    @Override
    @Transactional
    public PartsOrderModalResponseDTO open(PartsOrderOpenRequestDTO request) {
        TicketPart ticketPart = ticketPartDao.findById(request.getTicketPartId())
                .orElseThrow(() -> new ResourceNotFoundException("Ticket part not found: " + request.getTicketPartId()));

        validateApprovals(ticketPart);

        return partsOrderDao.findByTicketPart_TicketPartId(request.getTicketPartId())
                .map(existing -> partsOrderDao.findModalByOrderId(existing.getOrderId())
                        .orElseThrow(() -> new ResourceNotFoundException("Order modal not found")))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No order for ticket part: " + request.getTicketPartId()
                                + ". Save the order to create it."));
    }

    @Override
    @Transactional
    public PartsOrderModalResponseDTO create(PartsOrderSaveRequestDTO request) {
        if (request.getTicketPartId() == null) {
            throw new BadRequestException("ticketPartId is required");
        }

        TicketPart ticketPart = ticketPartDao.findById(request.getTicketPartId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ticket part not found: " + request.getTicketPartId()));

        validateApprovals(ticketPart);

        return partsOrderDao.findByTicketPart_TicketPartId(request.getTicketPartId())
                .map(existing -> save(existing.getOrderId(), request))
                .orElseGet(() -> {
                    PartsOrder order = createOrderHeader(request, ticketPart);
                    return save(order.getOrderId(), request);
                });
    }

    @Override
    @Transactional
    public PartsOrderModalResponseDTO save(Integer orderId, PartsOrderSaveRequestDTO request) {
        PartsOrder order = partsOrderDao.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

        List<PartsMasterLineRequestDTO> lines = request.getLines() != null ? request.getLines() : List.of();
        validateReceivedRequiresSerial(lines);

        Integer userId = InventoryAuditHelper.currentEmployeeId();
        if (request.getRemarks() != null) {
            order.setRemarks(request.getRemarks());
        }
        order.setUpdatedBy(userId);

        Set<Integer> processedIds = new HashSet<>();

        for (PartsMasterLineRequestDTO lineReq : lines) {
            PartsMaster master = resolveMasterLine(lineReq, order, userId);
            applyMasterLine(master, lineReq, order, userId);
            partsMasterDao.save(master);
            processedIds.add(master.getIndividualPartId());
        }

        syncLineCount(order, request.getQuantity(), userId, processedIds);
        recomputeOrderStatus(order, userId);
        updateTicketPartFromOrder(order);
        partsOrderDao.save(order);

        return partsOrderDao.findModalByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order modal not found after save"));
    }

    private void validateApprovals(TicketPart ticketPart) {
        if (!Boolean.TRUE.equals(ticketPart.getManagerApproval())) {
            throw new BadRequestException("Ticket part must be approved before ordering");
        }
        if (!Boolean.TRUE.equals(ticketPart.getCustomerApproval())) {
            throw new BadRequestException("Customer approval is required before ordering");
        }
    }

    private void validateReceivedRequiresSerial(List<PartsMasterLineRequestDTO> lines) {
        for (PartsMasterLineRequestDTO line : lines) {
            if (Boolean.TRUE.equals(line.getReceived())
                    && (line.getPartSrNo() == null || line.getPartSrNo().isBlank())) {
                throw new BadRequestException("Serial No is required when Received is checked");
            }
        }
    }

    private PartsOrder createOrderHeader(PartsOrderSaveRequestDTO request, TicketPart ticketPart) {
        int quantity = request.getQuantity() != null && request.getQuantity() > 0
                ? request.getQuantity()
                : (ticketPart.getQuantity() != null && ticketPart.getQuantity() > 0
                        ? ticketPart.getQuantity()
                        : 1);
        Integer userId = InventoryAuditHelper.currentEmployeeId();

        PartsOrder order = new PartsOrder();
        order.setTicket(ticketPart.getTicket());
        order.setProductList(ticketPart.getProductList());
        order.setTicketPart(ticketPart);
        order.setQuantity(quantity);
        order.setStatus(PartsOrderStatus.ORDERED);
        order.setOrderedAt(LocalDateTime.now());
        order.setOrderedBy(userId);
        order.setCreatedBy(userId);
        order.setUpdatedBy(userId);
        if (request.getRemarks() != null) {
            order.setRemarks(request.getRemarks());
        }
        return partsOrderDao.save(order);
    }

    @Override
    @Transactional
    public PartsOrderModalResponseDTO updateStatus(Integer orderId, PartsOrderStatusRequestDTO request) {
        PartsOrder order = partsOrderDao.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

        PartsOrderStatus status = request.getStatus();
        if (status != PartsOrderStatus.RECEIVED && status != PartsOrderStatus.CANCELLED) {
            throw new BadRequestException("Only RECEIVED or CANCELLED can be set manually");
        }

        Integer userId = InventoryAuditHelper.currentEmployeeId();
        order.setStatus(status);
        order.setUpdatedBy(userId);

        if (status == PartsOrderStatus.RECEIVED) {
            order.setReceivedAt(LocalDateTime.now());
            order.setReceivedBy(userId);
        }
        if (status == PartsOrderStatus.CANCELLED && request.getCancelReason() != null) {
            order.setCancelReason(request.getCancelReason());
        }

        partsOrderDao.save(order);
        updateTicketPartFromOrder(order);

        return partsOrderDao.findModalByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order modal not found after status update"));
    }

    private PartsMaster resolveMasterLine(PartsMasterLineRequestDTO lineReq, PartsOrder order, Integer userId) {
        if (lineReq.getIndividualPartId() != null) {
            PartsMaster master = partsMasterDao.findById(lineReq.getIndividualPartId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Parts master line not found: " + lineReq.getIndividualPartId()));
            if (master.getPartsOrder() == null
                    || !master.getPartsOrder().getOrderId().equals(order.getOrderId())) {
                throw new BadRequestException("Line does not belong to this order");
            }
            return master;
        }

        PartsMaster master = new PartsMaster();
        master.setPartsOrder(order);
        master.setProductList(order.getProductList());
        master.setTicket(order.getTicket());
        master.setSource(lineReq.getSource() != null ? lineReq.getSource() : PartsMasterSource.MARKET);
        master.setCreatedBy(userId);
        master.setUpdatedBy(userId);
        return master;
    }

    private void applyMasterLine(
            PartsMaster master,
            PartsMasterLineRequestDTO lineReq,
            PartsOrder order,
            Integer userId) {
        if (lineReq.getSource() != null) {
            master.setSource(lineReq.getSource());
        }
        if (lineReq.getPartSrNo() != null) {
            master.setPartSrNo(lineReq.getPartSrNo());
        }
        if (lineReq.getBarcode() != null) {
            master.setBarcode(lineReq.getBarcode());
        }
        master.setDamagedFlag(Boolean.TRUE.equals(lineReq.getDamagedFlag()));
        master.setReturnedFlag(Boolean.TRUE.equals(lineReq.getReturnedFlag()));
        master.setVendorDamageReturn(Boolean.TRUE.equals(lineReq.getVendorDamageReturn()));
        master.setRemarks(lineReq.getRemarks());
        master.setUpdatedBy(userId);

        if (lineReq.getReplacedId() != null) {
            PartsMaster replaced = partsMasterDao.findById(lineReq.getReplacedId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Replaced part not found: " + lineReq.getReplacedId()));
            master.setReplacedPart(replaced);
        }

        boolean wasReceived = Boolean.TRUE.equals(master.getReceived());
        boolean nowReceived = Boolean.TRUE.equals(lineReq.getReceived());

        if (lineReq.getSource() == PartsMasterSource.STOCK && lineReq.getStockPickIndividualPartId() != null) {
            if (nowReceived && !wasReceived) {
                consumeStock(lineReq.getStockPickIndividualPartId(), master, userId);
            } else if (!nowReceived) {
                master.setPartSrNo(lineReq.getPartSrNo());
            }
        }

        if (lineReq.getSource() == PartsMasterSource.MARKET) {
            upsertPartPrice(master, lineReq, userId);
        }

        if (Boolean.TRUE.equals(lineReq.getVendorDamageReturn())) {
            upsertVendorReturn(master, lineReq.getReturnPartSrNo(), order.getTicket(), userId);
        } else {
            vendorDamagePartReturnDao.findByPartsMasterIndividualPartId(master.getIndividualPartId())
                    .ifPresent(vendorDamagePartReturnDao::delete);
            master.setVendorDamageReturn(false);
        }

        if (nowReceived && !wasReceived) {
            master.setReceivedAt(LocalDateTime.now());
            master.setReceivedBy(userId);
        }
        master.setReceived(nowReceived);
    }

    private void consumeStock(Integer stockPickId, PartsMaster master, Integer userId) {
        InStockPart stock = inStockPartDao.findById(stockPickId)
                .orElseThrow(() -> new ResourceNotFoundException("In-stock part not found: " + stockPickId));

        if (!stock.getProductList().getPartCatId().equals(master.getProductList().getPartCatId())) {
            throw new BadRequestException("Stock part category does not match order line");
        }
        if (Boolean.TRUE.equals(stock.getReceived())) {
            throw new BadRequestException("Stock part already consumed");
        }

        master.setPartSrNo(stock.getPartSrNo());
        master.setBarcode(stock.getBarcode());
        master.setSource(PartsMasterSource.STOCK);

        partsMasterDao.save(master);

        partPriceDao.findByIndividualPartId(stockPickId).ifPresent(stockPrice -> {
            PartPrice price = partPriceDao.findByIndividualPartId(master.getIndividualPartId())
                    .orElseGet(PartPrice::new);
            price.setIndividualPartId(master.getIndividualPartId());
            price.setProductList(master.getProductList());
            price.setSalesPrice(stockPrice.getSalesPrice());
            price.setPurchasePrice(stockPrice.getPurchasePrice());
            price.setCurrency(stockPrice.getCurrency());
            price.setUpdatedBy(userId);
            if (price.getCreatedBy() == null) {
                price.setCreatedBy(userId);
            }
            partPriceDao.save(price);
        });

        inStockPartDao.delete(stock);
        decrementStockCount(master.getProductList().getPartCatId(), userId);
    }

    private void decrementStockCount(Integer partCatId, Integer userId) {
        stocksDao.findByProductListPartCatId(partCatId).ifPresent(stockRow -> {
            int current = stockRow.getStocks() != null ? stockRow.getStocks() : 0;
            stockRow.setStocks(Math.max(0, current - 1));
            stockRow.setUpdatedBy(userId);
            stocksDao.save(stockRow);
        });
    }

    private void upsertPartPrice(PartsMaster master, PartsMasterLineRequestDTO lineReq, Integer userId) {
        if (lineReq.getSalesPrice() == null && lineReq.getPurchasePrice() == null) {
            return;
        }
        partsMasterDao.save(master);

        PartPrice price = partPriceDao.findByIndividualPartId(master.getIndividualPartId())
                .orElseGet(PartPrice::new);
        price.setIndividualPartId(master.getIndividualPartId());
        price.setProductList(master.getProductList());
        if (lineReq.getSalesPrice() != null) {
            price.setSalesPrice(lineReq.getSalesPrice());
        }
        if (lineReq.getPurchasePrice() != null) {
            price.setPurchasePrice(lineReq.getPurchasePrice());
        }
        price.setUpdatedBy(userId);
        if (price.getCreatedBy() == null) {
            price.setCreatedBy(userId);
        }
        partPriceDao.save(price);
    }

    private void upsertVendorReturn(PartsMaster master, String returnPartSrNo, Ticket ticket, Integer userId) {
        partsMasterDao.save(master);

        VendorDamagePartReturn returnRow = vendorDamagePartReturnDao
                .findByPartsMasterIndividualPartId(master.getIndividualPartId())
                .orElseGet(VendorDamagePartReturn::new);
        returnRow.setPartsMaster(master);
        returnRow.setTicket(ticket);
        returnRow.setReturnPartSrNo(returnPartSrNo);
        returnRow.setUpdatedBy(userId);
        if (returnRow.getCreatedBy() == null) {
            returnRow.setCreatedBy(userId);
        }
        vendorDamagePartReturnDao.save(returnRow);
        master.setVendorDamageReturn(true);
    }

    private void syncLineCount(
            PartsOrder order,
            Integer requestedQuantity,
            Integer userId,
            Set<Integer> activeLineIds) {
        int targetQty = requestedQuantity != null && requestedQuantity > 0
                ? requestedQuantity
                : order.getQuantity();

        List<PartsMaster> activeLines = new ArrayList<>(
                partsMasterDao.findByPartsOrder_OrderIdAndIsActiveTrueOrderByIndividualPartIdAsc(order.getOrderId()));

        for (PartsMaster line : activeLines) {
            if (!activeLineIds.contains(line.getIndividualPartId())) {
                line.setIsActive(false);
                line.setUpdatedBy(userId);
                partsMasterDao.save(line);
            }
        }

        long currentActive = partsMasterDao.countByPartsOrder_OrderIdAndIsActiveTrue(order.getOrderId());
        while (currentActive < targetQty) {
            PartsMaster master = new PartsMaster();
            master.setPartsOrder(order);
            master.setProductList(order.getProductList());
            master.setTicket(order.getTicket());
            master.setSource(PartsMasterSource.MARKET);
            master.setCreatedBy(userId);
            master.setUpdatedBy(userId);
            partsMasterDao.save(master);
            currentActive++;
        }

        order.setQuantity((int) partsMasterDao.countByPartsOrder_OrderIdAndIsActiveTrue(order.getOrderId()));
    }

    private void recomputeOrderStatus(PartsOrder order, Integer userId) {
        if (order.getStatus() == PartsOrderStatus.CANCELLED) {
            return;
        }

        long total = partsMasterDao.countByPartsOrder_OrderIdAndIsActiveTrue(order.getOrderId());
        long received = partsMasterDao.countByPartsOrder_OrderIdAndIsActiveTrueAndReceivedTrue(order.getOrderId());

        if (total == 0) {
            order.setStatus(PartsOrderStatus.ORDERED);
            return;
        }
        if (received == 0) {
            order.setStatus(PartsOrderStatus.ORDERED);
        } else if (received < total) {
            order.setStatus(PartsOrderStatus.PARTIAL);
        } else {
            order.setStatus(PartsOrderStatus.RECEIVED);
            order.setReceivedAt(LocalDateTime.now());
            order.setReceivedBy(userId);
        }

        order.setTotalPrice(computeTotalPrice(order.getOrderId()));
    }

    private BigDecimal computeTotalPrice(Integer orderId) {
        return partsMasterDao.findLinesByOrderId(orderId).stream()
                .map(line -> {
                    BigDecimal purchase = line.getPurchasePrice();
                    BigDecimal sales = line.getSalesPrice();
                    return purchase != null ? purchase : sales;
                })
                .filter(price -> price != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void updateTicketPartFromOrder(PartsOrder order) {
        TicketPart ticketPart = order.getTicketPart();
        if (ticketPart == null) {
            return;
        }

        TicketPartStatus mapped = switch (order.getStatus()) {
            case ORDERED -> TicketPartStatus.ORDERED;
            case PARTIAL -> TicketPartStatus.PARTIAL;
            case RECEIVED -> TicketPartStatus.RECEIVED;
            case CANCELLED -> TicketPartStatus.CANCELLED;
        };
        ticketPart.setPartStatus(mapped);
        ticketPart.setUpdatedBy(InventoryAuditHelper.currentEmployeeId());
        ticketPartDao.save(ticketPart);
    }
}
