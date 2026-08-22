package com.mays.srm.inventory.dto.resDTO;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ProductListResponseDTO {

    private Integer partCatId;
    private Integer deviceTypeId;
    private String deviceTypeName;
    private Integer brandId;
    private String brandName;
    private String partName;
    private String sku;
    private String hsnCode;
    private String specification;
    private String descr;
    private Boolean isActive;
    private Integer stocks;
    private Integer minStock;
    private Integer maxStock;
    private Integer createdBy;
    private String createdByName;
    private Integer updatedBy;
    private String updatedByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ProductListResponseDTO(
            Integer partCatId,
            Integer deviceTypeId,
            String deviceTypeName,
            Integer brandId,
            String brandName,
            String partName,
            String sku,
            String hsnCode,
            String specification,
            String descr,
            Boolean isActive,
            Integer stocks,
            Integer minStock,
            Integer maxStock,
            Integer createdBy,
            String createdByName,
            Integer updatedBy,
            String updatedByName,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.partCatId = partCatId;
        this.deviceTypeId = deviceTypeId;
        this.deviceTypeName = deviceTypeName;
        this.brandId = brandId;
        this.brandName = brandName;
        this.partName = partName;
        this.sku = sku;
        this.hsnCode = hsnCode;
        this.specification = specification;
        this.descr = descr;
        this.isActive = isActive;
        this.stocks = stocks;
        this.minStock = minStock;
        this.maxStock = maxStock;
        this.createdBy = createdBy;
        this.createdByName = createdByName;
        this.updatedBy = updatedBy;
        this.updatedByName = updatedByName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
