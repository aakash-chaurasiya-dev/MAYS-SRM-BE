package com.mays.srm.service;

import com.mays.srm.entity.UserMaster;

import java.util.List;

public interface UserMasterService extends GenericService<UserMaster, String> {
    UserMaster findByMobileNo(String mobileNo);
    UserMaster findByEmail(String email);
    List<UserMaster> findByFirstName(String firstName); // Changed to List
    List<UserMaster> findByLastName(String lastName); // Changed to List
    List<UserMaster> findByBranchName(String branchName);
}