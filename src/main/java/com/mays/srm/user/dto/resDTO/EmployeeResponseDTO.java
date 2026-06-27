package com.mays.srm.user.dto.resDTO;
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

    public String getDepartmentName() {
        return this.departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
}
