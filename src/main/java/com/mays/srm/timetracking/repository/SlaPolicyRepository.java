package com.mays.srm.timetracking.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mays.srm.timetracking.entities.SlaPolicy;

@Repository
public interface SlaPolicyRepository extends JpaRepository<SlaPolicy, Long> {

    List<SlaPolicy> findByIsActiveTrue();

    Optional<SlaPolicy> findFirstByDepartmentDepartmentIdAndRoleAndIsActiveTrue(Integer departmentId, String role);

    Optional<SlaPolicy> findFirstByDepartmentDepartmentIdAndRoleIsNullAndIsActiveTrue(Integer departmentId);
}
