package com.mays.srm.billing.dto.resDTO;
import lombok.Data;

@Data
public class ChargeTypeResponseDTO {
    private Integer chargeTypeId;
    private String chargeName;
    private String chargeDescription;
    private String allowedDepartmentIds;
    private String allowedRoles;
    private Boolean customerVisibility;
    private Character accountingSide;
    private Boolean isLocked;
    private java.util.Date insertDate;
    private java.util.Date lastUpdateDate;
}
