package com.mays.srm.inventory.dto.resDTO;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class InStockPartOptionDTO {

    private Integer individualPartId;
    private Integer partCatId;
    private String partSrNo;
    private String barcode;
    private BigDecimal salesPrice;
    private BigDecimal purchasePrice;

    public InStockPartOptionDTO(
            Integer individualPartId,
            Integer partCatId,
            String partSrNo,
            String barcode,
            BigDecimal salesPrice,
            BigDecimal purchasePrice) {
        this.individualPartId = individualPartId;
        this.partCatId = partCatId;
        this.partSrNo = partSrNo;
        this.barcode = barcode;
        this.salesPrice = salesPrice;
        this.purchasePrice = purchasePrice;
    }
}
