package com.mays.srm.inventory.dto.resDTO;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class PartPriceCombinedResponseDTO {

    private Integer partCatId;
    private String partName;
    private String sku;
    private Integer salesPriceId;
    private BigDecimal salesPrice;
    private LocalDate salesEffectiveFrom;
    private LocalDate salesEffectiveTo;
    private Integer purchasePriceId;
    private BigDecimal purchasePrice;
    private LocalDate purchaseEffectiveFrom;
    private LocalDate purchaseEffectiveTo;
    private String currency;
    private String remarks;
    private Boolean isActive;
    private Integer createdBy;
    private String createdByName;
    private Integer updatedBy;
    private String updatedByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PartPriceCombinedResponseDTO(
            Integer partCatId,
            String partName,
            String sku,
            Integer salesPriceId,
            BigDecimal salesPrice,
            LocalDate salesEffectiveFrom,
            LocalDate salesEffectiveTo,
            Integer purchasePriceId,
            BigDecimal purchasePrice,
            LocalDate purchaseEffectiveFrom,
            LocalDate purchaseEffectiveTo,
            String currency,
            String remarks,
            Boolean isActive,
            Integer createdBy,
            String createdByName,
            Integer updatedBy,
            String updatedByName,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.partCatId = partCatId;
        this.partName = partName;
        this.sku = sku;
        this.salesPriceId = salesPriceId;
        this.salesPrice = salesPrice;
        this.salesEffectiveFrom = salesEffectiveFrom;
        this.salesEffectiveTo = salesEffectiveTo;
        this.purchasePriceId = purchasePriceId;
        this.purchasePrice = purchasePrice;
        this.purchaseEffectiveFrom = purchaseEffectiveFrom;
        this.purchaseEffectiveTo = purchaseEffectiveTo;
        this.currency = currency;
        this.remarks = remarks;
        this.isActive = isActive;
        this.createdBy = createdBy;
        this.createdByName = createdByName;
        this.updatedBy = updatedBy;
        this.updatedByName = updatedByName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
