package com.mays.srm.security.repository;


import com.mays.srm.security.entities.ActiveSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActiveSessionDao extends JpaRepository<ActiveSession, String> {
    List<ActiveSession> findByUser_UserId(Integer userId);
    List<ActiveSession> findByEmployee_EmployeeId(Integer employeeId);
    List<ActiveSession> findByVendor_Id(Integer vendorId);
    void deleteByExpiresAtBefore(LocalDateTime time);
}