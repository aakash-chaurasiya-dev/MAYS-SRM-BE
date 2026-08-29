package com.mays.srm.inventory.service.impl;

import com.mays.srm.device.entities.Brand;
import com.mays.srm.device.entities.DeviceType;
import com.mays.srm.device.repository.BrandDao;
import com.mays.srm.device.repository.DeviceTypeDao;
import com.mays.srm.exception.BadRequestException;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.inventory.dto.request.ProductListRequestDTO;
import com.mays.srm.inventory.dto.resDTO.ProductListOptionDTO;
import com.mays.srm.inventory.dto.resDTO.ProductListResponseDTO;
import com.mays.srm.inventory.entities.ProductList;
import com.mays.srm.inventory.entities.Stocks;
import com.mays.srm.inventory.repository.ProductListDao;
import com.mays.srm.inventory.repository.StocksDao;
import com.mays.srm.inventory.service.ProductListService;
import com.mays.srm.inventory.util.InventoryAuditHelper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@Service
public class ProductListServiceImpl implements ProductListService {

    private final ProductListDao productListDao;
    private final StocksDao stocksDao;
    private final DeviceTypeDao deviceTypeDao;
    private final BrandDao brandDao;
    private final TransactionTemplate transactionTemplate;

    public ProductListServiceImpl(
            ProductListDao productListDao,
            StocksDao stocksDao,
            DeviceTypeDao deviceTypeDao,
            BrandDao brandDao,
            PlatformTransactionManager transactionManager) {
        this.productListDao = productListDao;
        this.stocksDao = stocksDao;
        this.deviceTypeDao = deviceTypeDao;
        this.brandDao = brandDao;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Override
    public List<ProductListOptionDTO> search(String term, int limit) {
        return productListDao.searchOptions(term, limit > 0 ? limit : 50);
    }

    @Override
    public List<ProductListResponseDTO> getAll() {
        return productListDao.findAllDetails();
    }

    @Override
    public ProductListResponseDTO getById(Integer partCatId) {
        return productListDao.findDetailsById(partCatId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + partCatId));
    }

    @Override
    @Transactional
    public ProductListResponseDTO create(ProductListRequestDTO request) {
        if (request.getPartName() == null || request.getPartName().isBlank()) {
            throw new BadRequestException("Part name is required");
        }

        Integer userId = InventoryAuditHelper.currentEmployeeId();
        ProductList entity = new ProductList();
        applyRequest(entity, request, userId, true);
        productListDao.save(entity);
        upsertStocks(entity, request, userId);

        return getById(entity.getPartCatId());
    }

    @Override
    @Transactional
    public ProductListResponseDTO update(Integer partCatId, ProductListRequestDTO request) {
        ProductList entity = productListDao.findById(partCatId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + partCatId));

        Integer userId = InventoryAuditHelper.currentEmployeeId();
        applyRequest(entity, request, userId, false);
        productListDao.save(entity);
        upsertStocks(entity, request, userId);

        return getById(partCatId);
    }

    @Override
    public void delete(Integer partCatId) {
        if (!productListDao.existsById(partCatId)) {
            throw new ResourceNotFoundException("Product not found: " + partCatId);
        }

        try {
            transactionTemplate.executeWithoutResult(status -> {
                stocksDao.findByProductListPartCatId(partCatId).ifPresent(stocksDao::delete);
                ProductList entity = productListDao.findById(partCatId)
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + partCatId));
                productListDao.delete(entity);
                productListDao.flush();
            });
        } catch (DataIntegrityViolationException ex) {
            transactionTemplate.executeWithoutResult(status -> {
                ProductList entity = productListDao.findById(partCatId)
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + partCatId));
                entity.setIsActive(false);
                entity.setUpdatedBy(InventoryAuditHelper.currentEmployeeId());
                productListDao.save(entity);
            });
        }
    }

    @Override
    public void deleteBulk(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        for (Integer id : ids) {
            delete(id);
        }
    }

    private void applyRequest(ProductList entity, ProductListRequestDTO request, Integer userId, boolean creating) {
        if (request.getPartName() != null) {
            entity.setPartName(request.getPartName().trim());
        }
        if (request.getSku() != null) {
            entity.setSku(request.getSku().isBlank() ? null : request.getSku().trim());
        }
        if (request.getHsnCode() != null) {
            entity.setHsnCode(request.getHsnCode());
        }
        if (request.getSpecification() != null) {
            entity.setSpecification(request.getSpecification());
        }
        if (request.getDescr() != null) {
            entity.setDescr(request.getDescr());
        }
        if (request.getIsActive() != null) {
            entity.setIsActive(request.getIsActive());
        } else if (creating) {
            entity.setIsActive(true);
        }

        if (request.getDeviceTypeId() != null) {
            DeviceType deviceType = deviceTypeDao.findById(request.getDeviceTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Device type not found: " + request.getDeviceTypeId()));
            entity.setDeviceType(deviceType);
        } else if (creating) {
            entity.setDeviceType(null);
        }

        if (request.getBrandId() != null) {
            Brand brand = brandDao.findById(request.getBrandId())
                    .orElseThrow(() -> new ResourceNotFoundException("Brand not found: " + request.getBrandId()));
            entity.setBrand(brand);
        } else if (creating) {
            entity.setBrand(null);
        }

        entity.setUpdatedBy(userId);
        if (creating) {
            entity.setCreatedBy(userId);
        }
    }

    private void upsertStocks(ProductList product, ProductListRequestDTO request, Integer userId) {
        boolean hasStockFields = request.getStocks() != null
                || request.getMinStock() != null
                || request.getMaxStock() != null;
        if (!hasStockFields && stocksDao.findByProductListPartCatId(product.getPartCatId()).isEmpty()) {
            Stocks stocks = new Stocks();
            stocks.setProductList(product);
            stocks.setStocks(0);
            stocks.setCreatedBy(userId);
            stocks.setUpdatedBy(userId);
            stocksDao.save(stocks);
            return;
        }

        Stocks stocks = stocksDao.findByProductListPartCatId(product.getPartCatId())
                .orElseGet(Stocks::new);
        stocks.setProductList(product);
        if (request.getStocks() != null) {
            stocks.setStocks(request.getStocks());
        } else if (stocks.getStocks() == null) {
            stocks.setStocks(0);
        }
        if (request.getMinStock() != null) {
            stocks.setMinStock(request.getMinStock());
        }
        if (request.getMaxStock() != null) {
            stocks.setMaxStock(request.getMaxStock());
        }
        stocks.setUpdatedBy(userId);
        if (stocks.getCreatedBy() == null) {
            stocks.setCreatedBy(userId);
        }
        stocksDao.save(stocks);
    }
}
