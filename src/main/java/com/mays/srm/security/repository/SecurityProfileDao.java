package com.mays.srm.security.repository;
import com.mays.srm.security.entities.SecurityProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SecurityProfileDao extends JpaRepository<SecurityProfile, Integer> {
    Optional<SecurityProfile> findByUser_UserId(Integer userId);
    Optional<SecurityProfile> findByEmployee_EmployeeId(Integer employeeId);
    Optional<SecurityProfile> findByVendor_Id(Integer vendorId);
}