package com.mays.srm.organization.dto.resDTO;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StatusResponseDTO {
    private Integer statusId;
    private String statusName;
    private Integer statusFlg;
    private String statusDescription;
    private String statusType;
    private String allowedDepartmentIds;
    private String allowedRoles;
    private String slaTimerAction;

    private Boolean isLocked;
    private java.util.Date insertDate;
    private java.util.Date lastUpdateDate;
}
