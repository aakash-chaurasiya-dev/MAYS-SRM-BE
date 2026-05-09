package com.mays.srm.dao.custom;

import com.mays.srm.entity.Employee;

import java.util.List;

public interface EmployeeDaoCustom {
    List<Employee> findByDepartment(int departmentId);
    List<Employee> findByEmployeeName(String employeeName);

}
