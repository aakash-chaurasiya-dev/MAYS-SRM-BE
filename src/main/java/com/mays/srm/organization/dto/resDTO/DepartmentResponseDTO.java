package com.mays.srm.organization.dto.resDTO;
import lombok.Data;

@Data
public class DepartmentResponseDTO {
    private Integer departmentId;
    private String departmentName;
    private String departmentDescription;
    private String defaultRole;

    private Boolean isLocked;
    private java.util.Date insertDate;
    private java.util.Date lastUpdateDate;
}
