package com.mays.srm.inventory.service.impl;

import com.mays.srm.exception.BadRequestException;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.inventory.dto.request.PartPriceCombinedRequestDTO;
import com.mays.srm.inventory.dto.resDTO.PartPriceCombinedResponseDTO;
import com.mays.srm.inventory.entities.PartPurchasePrice;
import com.mays.srm.inventory.entities.PartSalesPrice;
import com.mays.srm.inventory.entities.ProductList;
import com.mays.srm.inventory.repository.PartPurchasePriceDao;
import com.mays.srm.inventory.repository.PartSalesPriceDao;
import com.mays.srm.inventory.repository.ProductListDao;
import com.mays.srm.inventory.service.PartPriceService;
import com.mays.srm.inventory.util.InventoryAuditHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PartPriceServiceImpl implements PartPriceService {

    private final PartSalesPriceDao partSalesPriceDao;
    private final PartPurchasePriceDao partPurchasePriceDao;
    private final ProductListDao productListDao;

    public PartPriceServiceImpl(
            PartSalesPriceDao partSalesPriceDao,
            PartPurchasePriceDao partPurchasePriceDao,
            ProductListDao productListDao) {
        this.partSalesPriceDao = partSalesPriceDao;
        this.partPurchasePriceDao = partPurchasePriceDao;
        this.productListDao = productListDao;
    }

    @Override
    public List<PartPriceCombinedResponseDTO> getAll() {
        return partSalesPriceDao.findAllCombined();
    }

    @Override
    public PartPriceCombinedResponseDTO getByPartCatId(Integer partCatId) {
        return partSalesPriceDao.findCombinedByPartCatId(partCatId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + partCatId));
    }

    @Override
    @Transactional
    public PartPriceCombinedResponseDTO upsert(PartPriceCombinedRequestDTO request) {
        if (request.getPartCatId() == null) {
            throw new BadRequestException("partCatId is required");
        }
        return savePrices(request.getPartCatId(), request);
    }

    @Override
    @Transactional
    public PartPriceCombinedResponseDTO update(Integer partCatId, PartPriceCombinedRequestDTO request) {
        return savePrices(partCatId, request);
    }

    @Override
    @Transactional
    public void delete(Integer partCatId) {
        ProductList product = productListDao.findById(partCatId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + partCatId));

        partSalesPriceDao.findByProductListPartCatId(product.getPartCatId())
                .ifPresent(partSalesPriceDao::delete);
        partPurchasePriceDao.findByProductListPartCatId(product.getPartCatId())
                .ifPresent(partPurchasePriceDao::delete);
    }

    private PartPriceCombinedResponseDTO savePrices(Integer partCatId, PartPriceCombinedRequestDTO request) {
        ProductList product = productListDao.findById(partCatId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + partCatId));

        Integer userId = InventoryAuditHelper.currentEmployeeId();
        String currency = request.getCurrency() != null && !request.getCurrency().isBlank()
                ? request.getCurrency()
                : "INR";
        Boolean isActive = request.getIsActive() != null ? request.getIsActive() : true;

        if (request.getSalesPrice() != null) {
            PartSalesPrice sales = partSalesPriceDao.findByProductListPartCatId(partCatId)
                    .orElseGet(PartSalesPrice::new);
            sales.setProductList(product);
            sales.setSalesPrice(request.getSalesPrice());
            sales.setCurrency(currency);
            if (request.getSalesEffectiveFrom() != null) {
                sales.setEffectiveFrom(request.getSalesEffectiveFrom());
            }
            if (request.getSalesEffectiveTo() != null) {
                sales.setEffectiveTo(request.getSalesEffectiveTo());
            }
            if (request.getRemarks() != null) {
                sales.setRemarks(request.getRemarks());
            }
            sales.setIsActive(isActive);
            sales.setUpdatedBy(userId);
            if (sales.getCreatedBy() == null) {
                sales.setCreatedBy(userId);
            }
            partSalesPriceDao.save(sales);
        }

        if (request.getPurchasePrice() != null) {
            PartPurchasePrice purchase = partPurchasePriceDao.findByProductListPartCatId(partCatId)
                    .orElseGet(PartPurchasePrice::new);
            purchase.setProductList(product);
            purchase.setPurchasePrice(request.getPurchasePrice());
            purchase.setCurrency(currency);
            if (request.getPurchaseEffectiveFrom() != null) {
                purchase.setEffectiveFrom(request.getPurchaseEffectiveFrom());
            }
            if (request.getPurchaseEffectiveTo() != null) {
                purchase.setEffectiveTo(request.getPurchaseEffectiveTo());
            }
            if (request.getRemarks() != null) {
                purchase.setRemarks(request.getRemarks());
            }
            purchase.setIsActive(isActive);
            purchase.setUpdatedBy(userId);
            if (purchase.getCreatedBy() == null) {
                purchase.setCreatedBy(userId);
            }
            partPurchasePriceDao.save(purchase);
        }

        if (request.getSalesPrice() == null && request.getPurchasePrice() == null) {
            throw new BadRequestException("At least one of salesPrice or purchasePrice is required");
        }

        return getByPartCatId(partCatId);
    }
}
