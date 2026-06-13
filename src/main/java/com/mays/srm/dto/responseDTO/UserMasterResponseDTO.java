package com.mays.srm.dto.responseDTO;

import lombok.Data;

@Data
public class UserMasterResponseDTO {
    // Excluded: password, branchDescription

    private Integer userId;
    private String mobileNo;
    private String firstName;
    private String lastName;
    private String emailId;
    private String address;
    private String branchName; // Include branchName for context
    private String role;
    private Boolean isActive;

    public String getBranchName() {
        return this.branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }
}
