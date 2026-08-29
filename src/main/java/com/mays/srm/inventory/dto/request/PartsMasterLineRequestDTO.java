package com.mays.srm.inventory.dto.request;

import com.mays.srm.inventory.enums.PartsMasterSource;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PartsMasterLineRequestDTO {
    private Integer individualPartId;
    private String partSrNo;
    private String barcode;
    private PartsMasterSource source;
    private Integer stockPickIndividualPartId;
    private Boolean damagedFlag;
    private Boolean returnedFlag;
    private Boolean vendorDamageReturn;
    private String returnPartSrNo;
    private Integer replacedId;
    private Boolean received;
    private BigDecimal salesPrice;
    private BigDecimal purchasePrice;
    private String remarks;
}
