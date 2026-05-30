package com.mays.srm.dto.responseDTO;

import lombok.Data;

@Data
public class UserMasterResponseDTO {
    // Excluded: password, branchDescription, role

    private Integer userId;
    private String mobileNo;
    private String firstName;
    private String lastName;
    private String emailId;
    private String address;
    private String branchName; // Include branchName for context
    private Boolean isActive;
}
