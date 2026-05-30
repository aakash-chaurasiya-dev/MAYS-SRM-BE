package com.mays.srm.dto.responseDTO;

import lombok.Data;

@Data
public class EmployeeResponseDTO {
    private Integer employeeId;
    private String employeeName;
    private String departmentName;
    private String vendor;
    private String address;
    private String pincode;
    private String city;
    private String email;
    private String mobileNo;
    private String role;
    private Boolean isActive;
}
