package com.mays.srm.organization.dto.request;
import lombok.Data;

@Data
public class StatusRequestDTO {
    private String statusName;
    private Integer statusFlg;
    private String statusDescription;
    private String statusType;
    private String allowedDepartmentIds;
    private String allowedRoles;
    private String slaTimerAction;
}
