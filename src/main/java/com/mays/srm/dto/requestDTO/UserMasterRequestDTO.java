package com.mays.srm.dto.requestDTO;

import lombok.Data;

@Data
public class UserMasterRequestDTO {
    // Excluded: userId, branchName, branchDescription, role, isActive
    
    private String mobileNo;
    private String firstName;
    private String lastName;
    private String emailId;
    private String password;
    private String address;
    private Integer branchId; // Used to link to a branch, but without the name/description
}
