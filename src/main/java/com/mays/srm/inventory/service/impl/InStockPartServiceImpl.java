package com.mays.srm.inventory.service.impl;

import com.mays.srm.exception.BadRequestException;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.inventory.dto.request.InStockPartRequestDTO;
import com.mays.srm.inventory.dto.resDTO.InStockPartOptionDTO;
import com.mays.srm.inventory.dto.resDTO.InStockPartResponseDTO;
import com.mays.srm.inventory.entities.InStockPart;
import com.mays.srm.inventory.entities.PartPrice;
import com.mays.srm.inventory.entities.ProductList;
import com.mays.srm.inventory.entities.Stocks;
import com.mays.srm.inventory.enums.PartsMasterSource;
import com.mays.srm.inventory.repository.InStockPartDao;
import com.mays.srm.inventory.repository.PartPriceDao;
import com.mays.srm.inventory.repository.ProductListDao;
import com.mays.srm.inventory.repository.StocksDao;
import com.mays.srm.inventory.service.InStockPartService;
import com.mays.srm.inventory.util.InventoryAuditHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InStockPartServiceImpl implements InStockPartService {

    private final InStockPartDao inStockPartDao;
    private final ProductListDao productListDao;
    private final PartPriceDao partPriceDao;
    private final StocksDao stocksDao;

    public InStockPartServiceImpl(
            InStockPartDao inStockPartDao,
            ProductListDao productListDao,
            PartPriceDao partPriceDao,
            StocksDao stocksDao) {
        this.inStockPartDao = inStockPartDao;
        this.productListDao = productListDao;
        this.partPriceDao = partPriceDao;
        this.stocksDao = stocksDao;
    }

    @Override
    public List<InStockPartOptionDTO> findAvailable(Integer partCatId) {
        return inStockPartDao.findAvailableOptions(partCatId);
    }

    @Override
    public List<InStockPartResponseDTO> getAll(Integer partCatId) {
        return inStockPartDao.findAllDetails(partCatId);
    }

    @Override
    public InStockPartResponseDTO getById(Integer individualPartId) {
        return inStockPartDao.findDetailsById(individualPartId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "In-stock part not found: " + individualPartId));
    }

    @Override
    @Transactional
    public InStockPartResponseDTO create(InStockPartRequestDTO request) {
        if (request.getPartCatId() == null) {
            throw new BadRequestException("partCatId is required");
        }

        ProductList product = productListDao.findById(request.getPartCatId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found: " + request.getPartCatId()));

        Integer userId = InventoryAuditHelper.currentEmployeeId();
        InStockPart entity = new InStockPart();
        entity.setProductList(product);
        applyFields(entity, request, userId, true);
        inStockPartDao.save(entity);

        upsertPartPrice(entity, request, userId);
        incrementStockCount(product.getPartCatId(), userId);

        return getById(entity.getIndividualPartId());
    }

    @Override
    @Transactional
    public InStockPartResponseDTO update(Integer individualPartId, InStockPartRequestDTO request) {
        InStockPart entity = inStockPartDao.findById(individualPartId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "In-stock part not found: " + individualPartId));

        Integer userId = InventoryAuditHelper.currentEmployeeId();

        if (request.getPartCatId() != null
                && !request.getPartCatId().equals(entity.getProductList().getPartCatId())) {
            ProductList product = productListDao.findById(request.getPartCatId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product not found: " + request.getPartCatId()));
            entity.setProductList(product);
        }

        applyFields(entity, request, userId, false);
        inStockPartDao.save(entity);
        upsertPartPrice(entity, request, userId);

        return getById(individualPartId);
    }

    @Override
    @Transactional
    public void delete(Integer individualPartId) {
        InStockPart entity = inStockPartDao.findById(individualPartId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "In-stock part not found: " + individualPartId));

        Integer partCatId = entity.getProductList().getPartCatId();
        Integer userId = InventoryAuditHelper.currentEmployeeId();

        partPriceDao.findByIndividualPartId(individualPartId).ifPresent(partPriceDao::delete);
        inStockPartDao.delete(entity);
        decrementStockCount(partCatId, userId);
    }

    @Override
    @Transactional
    public void deleteBulk(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        for (Integer id : ids) {
            delete(id);
        }
    }

    private void applyFields(InStockPart entity, InStockPartRequestDTO request, Integer userId, boolean creating) {
        if (request.getPartSrNo() != null) {
            entity.setPartSrNo(request.getPartSrNo());
        }
        if (request.getBarcode() != null) {
            entity.setBarcode(request.getBarcode());
        }
        if (request.getSource() != null) {
            PartsMasterSource source = PartsMasterSource.from(request.getSource());
            if (source == null || (source != PartsMasterSource.MARKET && source != PartsMasterSource.VENDOR)) {
                throw new BadRequestException("source must be MARKET or VENDOR");
            }
            entity.setSource(source);
        } else if (creating) {
            entity.setSource(PartsMasterSource.MARKET);
        }

        if (request.getReceived() != null) {
            entity.setReceived(request.getReceived());
            if (Boolean.TRUE.equals(request.getReceived()) && entity.getReceivedAt() == null) {
                entity.setReceivedAt(LocalDateTime.now());
            }
            if (Boolean.FALSE.equals(request.getReceived())) {
                entity.setReceivedAt(null);
            }
        } else if (creating) {
            entity.setReceived(false);
        }

        if (request.getRemarks() != null) {
            entity.setRemarks(request.getRemarks());
        }

        if (creating) {
            entity.setIsActive(true);
            entity.setCreatedBy(userId);
        }
        entity.setUpdatedBy(userId);
    }

    private void upsertPartPrice(InStockPart entity, InStockPartRequestDTO request, Integer userId) {
        if (request.getSalesPrice() == null && request.getPurchasePrice() == null) {
            return;
        }

        PartPrice price = partPriceDao.findByIndividualPartId(entity.getIndividualPartId())
                .orElseGet(PartPrice::new);
        price.setIndividualPartId(entity.getIndividualPartId());
        price.setProductList(entity.getProductList());
        if (request.getSalesPrice() != null) {
            price.setSalesPrice(request.getSalesPrice());
        }
        if (request.getPurchasePrice() != null) {
            price.setPurchasePrice(request.getPurchasePrice());
        }
        if (price.getCurrency() == null) {
            price.setCurrency("INR");
        }
        price.setUpdatedBy(userId);
        if (price.getCreatedBy() == null) {
            price.setCreatedBy(userId);
        }
        partPriceDao.save(price);
    }

    private void incrementStockCount(Integer partCatId, Integer userId) {
        Stocks stocks = stocksDao.findByProductListPartCatId(partCatId).orElseGet(() -> {
            Stocks created = new Stocks();
            ProductList product = productListDao.findById(partCatId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + partCatId));
            created.setProductList(product);
            created.setStocks(0);
            created.setCreatedBy(userId);
            return created;
        });
        int current = stocks.getStocks() != null ? stocks.getStocks() : 0;
        stocks.setStocks(current + 1);
        stocks.setUpdatedBy(userId);
        stocksDao.save(stocks);
    }

    private void decrementStockCount(Integer partCatId, Integer userId) {
        stocksDao.findByProductListPartCatId(partCatId).ifPresent(stockRow -> {
            int current = stockRow.getStocks() != null ? stockRow.getStocks() : 0;
            stockRow.setStocks(Math.max(0, current - 1));
            stockRow.setUpdatedBy(userId);
            stocksDao.save(stockRow);
        });
    }
}
