package com.mays.srm.user.dto.resDTO;
import lombok.Data;

@Data
public class EmployeeSpecResponseDTO {
    private Integer employeeId;
    private String employeeName;
    private Integer deviceTypeId;
    private String deviceTypeName;
}
