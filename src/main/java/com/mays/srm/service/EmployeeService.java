package com.mays.srm.service;

import com.mays.srm.entity.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeService extends GenericService<Employee, Integer> {
    Optional<Employee> findEmployeeByMobileNo(String mobileNo);
    Optional<Employee> findEmployeeByEmail(String email);
    List<Employee> findByEmployeeName(String employeeName);
    List<Employee> findEmployeeByDepartment(int departmentId);
}