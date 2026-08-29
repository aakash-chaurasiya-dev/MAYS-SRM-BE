package com.mays.srm.billing.dto.request;
import lombok.Data;

@Data
public class ChargeTypeRequestDTO {
    private String chargeName;
    private String chargeDescription;
    private String allowedDepartmentIds;
    private Boolean customerVisibility;
    private Character accountingSide;
    private Boolean isLocked;
}
