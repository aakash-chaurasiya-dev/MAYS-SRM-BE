package com.mays.srm.inventory.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PartPriceCombinedRequestDTO {
    private Integer partCatId;
    private BigDecimal salesPrice;
    private BigDecimal purchasePrice;
    private String currency;
    private LocalDate salesEffectiveFrom;
    private LocalDate salesEffectiveTo;
    private LocalDate purchaseEffectiveFrom;
    private LocalDate purchaseEffectiveTo;
    private String remarks;
    private Boolean isActive;
}
