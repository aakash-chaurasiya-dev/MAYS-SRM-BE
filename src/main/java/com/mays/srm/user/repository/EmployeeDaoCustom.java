package com.mays.srm.user.repository;
import com.mays.srm.user.entities.Employee;

import java.util.List;

public interface EmployeeDaoCustom {
    List<Employee> findByDepartment(int departmentId);
    List<Employee> findByEmployeeName(String employeeName);

}

