package com.mays.srm.inventory.dto.resDTO;

import com.mays.srm.inventory.enums.PartsMasterSource;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class InStockPartResponseDTO {

    private Integer individualPartId;
    private Integer partCatId;
    private String partName;
    private String sku;
    private String partSrNo;
    private String barcode;
    private PartsMasterSource source;
    private Boolean received;
    private LocalDateTime receivedAt;
    private String remarks;
    private Boolean isActive;
    private Integer partPriceId;
    private BigDecimal salesPrice;
    private BigDecimal purchasePrice;
    private String currency;
    private Integer createdBy;
    private String createdByName;
    private Integer updatedBy;
    private String updatedByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public InStockPartResponseDTO(
            Integer individualPartId,
            Integer partCatId,
            String partName,
            String sku,
            String partSrNo,
            String barcode,
            String source,
            Boolean received,
            LocalDateTime receivedAt,
            String remarks,
            Boolean isActive,
            Integer partPriceId,
            BigDecimal salesPrice,
            BigDecimal purchasePrice,
            String currency,
            Integer createdBy,
            String createdByName,
            Integer updatedBy,
            String updatedByName,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.individualPartId = individualPartId;
        this.partCatId = partCatId;
        this.partName = partName;
        this.sku = sku;
        this.partSrNo = partSrNo;
        this.barcode = barcode;
        this.source = PartsMasterSource.from(source);
        this.received = received;
        this.receivedAt = receivedAt;
        this.remarks = remarks;
        this.isActive = isActive;
        this.partPriceId = partPriceId;
        this.salesPrice = salesPrice;
        this.purchasePrice = purchasePrice;
        this.currency = currency;
        this.createdBy = createdBy;
        this.createdByName = createdByName;
        this.updatedBy = updatedBy;
        this.updatedByName = updatedByName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
