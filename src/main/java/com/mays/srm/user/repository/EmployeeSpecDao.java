package com.mays.srm.user.repository;
import com.mays.srm.user.entities.EmployeeSpec;
import com.mays.srm.user.entities.EmployeeSpecId;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface EmployeeSpecDao extends JpaRepository<EmployeeSpec, EmployeeSpecId> {
}
