package com.mays.srm.user.dto.request;
import lombok.Data;

@Data
public class EmployeeRequestDTO {
    private String employeeName;
    private Integer departmentId;
    private String vendor;
    private String address;
    private String pincode;
    private String city;
    private String email;
    private String mobileNo;
    private Boolean isActive;
    private String password;
}
