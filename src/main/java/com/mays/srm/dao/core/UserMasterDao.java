package com.mays.srm.dao.core;

import com.mays.srm.dao.custom.UserMasterDaoCustom;
import com.mays.srm.entity.UserMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserMasterDao extends JpaRepository<UserMaster, String>, UserMasterDaoCustom {
    Optional<UserMaster> findByMobileNo(String mobileNo);
    Optional<UserMaster> findByEmailId(String emailId); // Fixed to return UserMaster and match entity field
    List<UserMaster> findByFirstName(String firstName); // Better as List since many users can have same first name
    List<UserMaster> findByLastName(String lastName); // Better as List
    List<UserMaster> findByBranchBranchName(String branchName); // Match entity mapping branch.branchName
}
