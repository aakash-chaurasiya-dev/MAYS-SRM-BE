package com.mays.srm.inventory.dto.resDTO;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class ProductListOptionDTO {

    private Integer partCatId;
    private String partName;
    private String sku;
    private String deviceTypeName;
    private String brandName;
    private BigDecimal defaultSalesPrice;
    private BigDecimal defaultPurchasePrice;

    public ProductListOptionDTO(
            Integer partCatId,
            String partName,
            String sku,
            String deviceTypeName,
            String brandName,
            BigDecimal defaultSalesPrice,
            BigDecimal defaultPurchasePrice) {
        this.partCatId = partCatId;
        this.partName = partName;
        this.sku = sku;
        this.deviceTypeName = deviceTypeName;
        this.brandName = brandName;
        this.defaultSalesPrice = defaultSalesPrice;
        this.defaultPurchasePrice = defaultPurchasePrice;
    }
}
