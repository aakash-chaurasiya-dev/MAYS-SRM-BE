package com.mays.srm.dao.core;

import com.mays.srm.dao.custom.EmployeeDaoCustom;
import com.mays.srm.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeDao extends JpaRepository<Employee, Integer>, EmployeeDaoCustom {
    Optional<Employee> findByMobileNo(String mobileNo);
    Optional<Employee> findByEmail(String email);
}
