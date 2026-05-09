package com.mays.srm.dao.core;

import com.mays.srm.entity.EmployeeSpec;
import com.mays.srm.entity.EmployeeSpecId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeSpecDao extends JpaRepository<EmployeeSpec, EmployeeSpecId> {
}
