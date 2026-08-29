package com.mays.srm.inventory.dto.request;

import lombok.Data;

@Data
public class ProductListRequestDTO {
    private Integer partCatId;
    private Integer deviceTypeId;
    private Integer brandId;
    private String partName;
    private String sku;
    private String hsnCode;
    private String specification;
    private String descr;
    private Boolean isActive;
    private Integer stocks;
    private Integer minStock;
    private Integer maxStock;
}
