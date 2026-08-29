package com.mays.srm.inventory.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InStockPartRequestDTO {
    private Integer individualPartId;
    private Integer partCatId;
    private String partSrNo;
    private String barcode;
    private String source;
    private Boolean received;
    private String remarks;
    private BigDecimal salesPrice;
    private BigDecimal purchasePrice;
}
