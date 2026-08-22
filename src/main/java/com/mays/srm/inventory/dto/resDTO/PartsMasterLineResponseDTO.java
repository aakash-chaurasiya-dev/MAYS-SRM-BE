package com.mays.srm.inventory.dto.resDTO;

import com.mays.srm.inventory.enums.PartsMasterSource;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class PartsMasterLineResponseDTO {

    private Integer individualPartId;
    private Integer orderId;
    private String partSrNo;
    private String barcode;
    private PartsMasterSource source;
    private Boolean damagedFlag;
    private Boolean returnedFlag;
    private Boolean vendorDamageReturn;
    private String returnPartSrNo;
    private Integer replacedId;
    private String replacedPartSrNo;
    private Boolean received;
    private LocalDateTime receivedAt;
    private BigDecimal salesPrice;
    private BigDecimal purchasePrice;
    private Boolean isActive;

    public PartsMasterLineResponseDTO(
            Integer individualPartId,
            Integer orderId,
            String partSrNo,
            String barcode,
            String source,
            Boolean damagedFlag,
            Boolean returnedFlag,
            Boolean vendorDamageReturn,
            String returnPartSrNo,
            Integer replacedId,
            String replacedPartSrNo,
            Boolean received,
            LocalDateTime receivedAt,
            BigDecimal salesPrice,
            BigDecimal purchasePrice,
            Boolean isActive) {
        this.individualPartId = individualPartId;
        this.orderId = orderId;
        this.partSrNo = partSrNo;
        this.barcode = barcode;
        this.source = PartsMasterSource.from(source);
        this.damagedFlag = damagedFlag;
        this.returnedFlag = returnedFlag;
        this.vendorDamageReturn = vendorDamageReturn;
        this.returnPartSrNo = returnPartSrNo;
        this.replacedId = replacedId;
        this.replacedPartSrNo = replacedPartSrNo;
        this.received = received;
        this.receivedAt = receivedAt;
        this.salesPrice = salesPrice;
        this.purchasePrice = purchasePrice;
        this.isActive = isActive;
    }
}
