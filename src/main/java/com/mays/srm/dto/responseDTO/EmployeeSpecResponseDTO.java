package com.mays.srm.dto.responseDTO;

import lombok.Data;

@Data
public class EmployeeSpecResponseDTO {
    private Integer employeeId;
    private String employeeName;
    private Integer deviceTypeId;
    private String deviceTypeName;
}
