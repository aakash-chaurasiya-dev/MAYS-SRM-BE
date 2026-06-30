package com.mays.srm.user.repository;
import com.mays.srm.user.repository.UserMasterDaoCustom;
import com.mays.srm.user.entities.UserMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserMasterDao extends JpaRepository<UserMaster, Integer>, UserMasterDaoCustom {
    Optional<UserMaster> findByMobileNo(String mobileNo);
    Optional<UserMaster> findByEmailId(String emailId); // Fixed to return UserMaster and match entity field
    List<UserMaster> findByFirstName(String firstName); // Better as List since many users can have same first name
    List<UserMaster> findByLastName(String lastName); // Better as List
}

