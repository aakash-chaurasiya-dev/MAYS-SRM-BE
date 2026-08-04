package com.mays.srm.inventory.service.impl;

import com.mays.srm.exception.BadRequestException;
import com.mays.srm.exception.InternalServerException;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.inventory.dto.request.PartsRequestDTO;
import com.mays.srm.inventory.dto.resDTO.PartsResponseDTO;
import com.mays.srm.inventory.entities.Inventory;
import com.mays.srm.inventory.entities.InventoryLog;
import com.mays.srm.inventory.entities.Parts;
import com.mays.srm.inventory.enums.InventoryLogReason;
import com.mays.srm.inventory.enums.PartSource;
import com.mays.srm.inventory.repository.InventoryDao;
import com.mays.srm.inventory.repository.InventoryLogDao;
import com.mays.srm.inventory.repository.PartsDao;
import com.mays.srm.inventory.service.PartsService;
import com.mays.srm.organization.entities.Status;
import com.mays.srm.organization.repository.StatusDao;
import com.mays.srm.security.util.SecurityUtils;
import com.mays.srm.ticket.entities.Ticket;
import com.mays.srm.ticket.repository.TicketDao;
import com.mays.srm.user.entities.Vendor;
import com.mays.srm.user.repository.VendorDao;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PartsServiceImpl implements PartsService {

    private final PartsDao repository;
    private final TicketDao ticketDao;
    private final InventoryDao inventoryDao;
    private final InventoryLogDao inventoryLogDao;
    private final StatusDao statusDao;
    private final VendorDao vendorDao;
    private final ModelMapper modelMapper;

    @Autowired
    public PartsServiceImpl(
            PartsDao repository,
            TicketDao ticketDao,
            InventoryDao inventoryDao,
            InventoryLogDao inventoryLogDao,
            StatusDao statusDao,
            VendorDao vendorDao,
            ModelMapper modelMapper
    ) {
        this.repository = repository;
        this.ticketDao = ticketDao;
        this.inventoryDao = inventoryDao;
        this.inventoryLogDao = inventoryLogDao;
        this.statusDao = statusDao;
        this.vendorDao = vendorDao;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    @CacheEvict(value = "inventory", allEntries = true)
    public PartsResponseDTO create(PartsRequestDTO requestDTO) {
        try {
            Parts part = new Parts();
            applyRequest(part, requestDTO, true);
            Parts savedPart = repository.save(part);
            applyStockSideEffects(savedPart, null);
            savedPart = repository.save(savedPart);
            return mapToResponseDTO(savedPart);
        } catch (ResourceNotFoundException | DataIntegrityViolationException | BadRequestException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while creating Part", ex);
        }
    }

    @Override
    public PartsResponseDTO getById(Integer id) {
        return repository.findById(id)
                .map(this::mapToResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Part not found with ID: " + id));
    }

    @Override
    public List<PartsResponseDTO> getAll() {
        List<PartsResponseDTO> dtoList = new ArrayList<>();
        for (Parts part : repository.findAll()) {
            dtoList.add(mapToResponseDTO(part));
        }
        return dtoList;
    }

    @Override
    public List<PartsResponseDTO> getByTicketId(Integer ticketId) {
        List<PartsResponseDTO> dtoList = new ArrayList<>();
        for (Parts part : repository.findByTicket_TicketId(ticketId)) {
            dtoList.add(mapToResponseDTO(part));
        }
        return dtoList;
    }

    @CacheEvict(value = "inventory", allEntries = true)
    @Override
    @Transactional
    public PartsResponseDTO update(Integer id, PartsRequestDTO requestDTO) {
        Parts existingPart = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot update. Part not found with ID: " + id));

        String previousStatusName = existingPart.getStatus() != null
                ? existingPart.getStatus().getStatusName()
                : null;

        try {
            applyRequest(existingPart, requestDTO, false);
            existingPart.setPartId(id);
            applyStockSideEffects(existingPart, previousStatusName);
            Parts updatedPart = repository.save(existingPart);
            return mapToResponseDTO(updatedPart);
        } catch (ResourceNotFoundException | DataIntegrityViolationException | BadRequestException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while updating Part", ex);
        }
    }

    @Override
    public void delete(Integer id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete. Part not found with ID: " + id);
        }
        try {
            repository.deleteById(id);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while deleting Part with ID: " + id, ex);
        }
    }

    private void applyRequest(Parts part, PartsRequestDTO requestDTO, boolean isCreate) {
        PartSource source = resolveSource(requestDTO.getSource());
        if (source == null) {
            throw new BadRequestException("source is required (VENDOR, MARKET, STOCK_IN, STOCK_OUT)");
        }
        part.setSource(source);

        if (source == PartSource.STOCK_IN) {
            part.setTicket(null);
        } else {
            if (requestDTO.getTicketId() == null) {
                throw new BadRequestException("ticketId is required for source " + source);
            }
            Ticket ticket = ticketDao.findById(requestDTO.getTicketId())
                    .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with ID: " + requestDTO.getTicketId()));
            part.setTicket(ticket);
        }

        if (requestDTO.getQuantity() == null || requestDTO.getQuantity() < 1) {
            throw new BadRequestException("quantity must be at least 1");
        }
        part.setQuantity(requestDTO.getQuantity());

        resolveAndSetProduct(part, requestDTO);

        if (requestDTO.getStatusId() != null) {
            Status status = statusDao.findById(requestDTO.getStatusId())
                    .orElseThrow(() -> new ResourceNotFoundException("Status not found with ID: " + requestDTO.getStatusId()));
            if (!"PARTS".equalsIgnoreCase(status.getStatusType())) {
                throw new BadRequestException("Status has to be of type PARTS: " + requestDTO.getStatusId());
            }
            part.setStatus(status);
        } else if (isCreate) {
            throw new BadRequestException("statusId is required");
        }

        if (requestDTO.getVendorId() != null) {
            Vendor vendor = vendorDao.findById(requestDTO.getVendorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with ID: " + requestDTO.getVendorId()));
            part.setVendor(vendor);
        } else if (isCreate) {
            part.setVendor(null);
        }

        part.setUnitCost(requestDTO.getUnitCost());
        part.setPartName(resolvePartName(part, requestDTO));
        part.setRemarks(requestDTO.getRemarks());
        part.setReceiveDate(requestDTO.getReceiveDate());
        part.setUsedDate(requestDTO.getUsedDate());
        part.setReturnDate(requestDTO.getReturnDate());
        part.setCustomerApproved(requestDTO.getCustomerApproved());

        Boolean defective = requestDTO.getDefectiveReturned();
        if (defective == null) {
            defective = requestDTO.getReturned();
        }
        if (defective != null) {
            part.setDefectiveReturned(defective);
        } else if (isCreate) {
            part.setDefectiveReturned(false);
        }

        if (isCreate && part.getStockApplied() == null) {
            part.setStockApplied(false);
        }
    }

    private PartSource resolveSource(String sourceValue) {
        try {
            return PartSource.from(sourceValue);
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Invalid source: " + sourceValue + ". Allowed: VENDOR, MARKET, STOCK_IN, STOCK_OUT");
        }
    }

    private void resolveAndSetProduct(Parts part, PartsRequestDTO requestDTO) {
        if (requestDTO.getProductId() != null) {
            Inventory inventory = inventoryDao.findById(requestDTO.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + requestDTO.getProductId()));
            part.setProduct(inventory);
            return;
        }

        String name = requestDTO.getPartName();
        if (name == null || name.isBlank()) {
            if (part.getSource() == PartSource.STOCK_OUT || part.getSource() == PartSource.STOCK_IN) {
                throw new BadRequestException("productId is required for " + part.getSource());
            }
            part.setProduct(null);
            return;
        }

        Optional<Inventory> existing = inventoryDao.findFirstByProductNameIgnoreCase(name.trim());
        if (existing.isPresent()) {
            part.setProduct(existing.get());
            return;
        }

        // Catalog upsert with stock 0 for future suggestions (no qty change yet)
        Inventory catalog = new Inventory();
        catalog.setProductName(name.trim());
        catalog.setStock(0);
        catalog.setIsActive(true);
        part.setProduct(inventoryDao.save(catalog));
    }

    private String resolvePartName(Parts part, PartsRequestDTO requestDTO) {
        if (requestDTO.getPartName() != null && !requestDTO.getPartName().isBlank()) {
            return requestDTO.getPartName().trim();
        }
        if (part.getProduct() != null) {
            return part.getProduct().getProductName();
        }
        return null;
    }

    private void applyStockSideEffects(Parts part, String previousStatusName) {
        PartSource source = part.getSource();
        if (source != PartSource.STOCK_IN && source != PartSource.STOCK_OUT) {
            return;
        }
        if (Boolean.TRUE.equals(part.getStockApplied())) {
            return;
        }

        String statusName = part.getStatus() != null ? part.getStatus().getStatusName() : null;
        if (statusName == null) {
            return;
        }

        boolean shouldReceive = source == PartSource.STOCK_IN && isReceiveStatus(statusName);
        boolean shouldConsume = source == PartSource.STOCK_OUT && isUsedStatus(statusName);

        // Only apply when newly reaching the trigger status (create or transition)
        if (previousStatusName != null) {
            if (shouldReceive && isReceiveStatus(previousStatusName)) {
                return;
            }
            if (shouldConsume && isUsedStatus(previousStatusName)) {
                return;
            }
        }

        if (!shouldReceive && !shouldConsume) {
            return;
        }

        Inventory product = part.getProduct();
        if (product == null) {
            throw new BadRequestException("product is required to apply stock changes for " + source);
        }

        int qty = part.getQuantity() != null ? part.getQuantity() : 0;
        int current = product.getStock() != null ? product.getStock() : 0;

        if (shouldReceive) {
            int balance = current + qty;
            product.setStock(balance);
            inventoryDao.save(product);
            writeLog(product, qty, balance, InventoryLogReason.RECEIVE, part);
            if (part.getReceiveDate() == null) {
                part.setReceiveDate(LocalDateTime.now());
            }
            part.setStockApplied(true);
        } else if (shouldConsume) {
            if (current < qty) {
                throw new BadRequestException("Insufficient stock for product '" + product.getProductName()
                        + "'. Available: " + current + ", required: " + qty);
            }
            int balance = current - qty;
            product.setStock(balance);
            inventoryDao.save(product);
            writeLog(product, -qty, balance, InventoryLogReason.CONSUME, part);
            if (part.getUsedDate() == null) {
                part.setUsedDate(LocalDateTime.now());
            }
            part.setStockApplied(true);
        }
    }

    private boolean isReceiveStatus(String statusName) {
        String n = statusName.trim().toLowerCase();
        return n.equals("received") || n.equals("delivered");
    }

    private boolean isUsedStatus(String statusName) {
        String n = statusName.trim().toLowerCase();
        return n.equals("used") || n.equals("fitted") || n.equals("delivered") || n.equals("consumed");
    }

    private void writeLog(Inventory product, int changeQty, int balanceAfter, InventoryLogReason reason, Parts order) {
        InventoryLog log = new InventoryLog();
        log.setProduct(product);
        log.setBranch(product.getBranch());
        log.setChangeQty(changeQty);
        log.setBalanceAfter(balanceAfter);
        log.setReason(reason);
        log.setOrder(order);
        SecurityUtils.getCurrentUser().ifPresent(u -> log.setCreatedBy(String.valueOf(u.getUserId())));
        inventoryLogDao.save(log);
    }

    private PartsResponseDTO mapToResponseDTO(Parts part) {
        PartsResponseDTO dto = modelMapper.map(part, PartsResponseDTO.class);
        if (part.getTicket() != null) {
            dto.setTicketId(part.getTicket().getTicketId());
        }
        if (part.getProduct() != null) {
            dto.setProductId(part.getProduct().getProductId());
            dto.setProductName(part.getProduct().getProductName());
        }
        if (part.getStatus() != null) {
            dto.setStatusId(part.getStatus().getStatusId());
            dto.setStatusName(part.getStatus().getStatusName());
        }
        if (part.getSource() != null) {
            dto.setSource(part.getSource().name());
        }
        if (part.getVendor() != null) {
            dto.setVendorId(part.getVendor().getId());
            dto.setVendorName(part.getVendor().getName());
        }
        return dto;
    }
}
