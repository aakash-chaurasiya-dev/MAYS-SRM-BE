package com.mays.srm.service.impl;

import com.mays.srm.dao.core.DepartmentDao;
import com.mays.srm.dao.core.EmployeeDao;
import com.mays.srm.entity.Department;
import com.mays.srm.entity.Employee;
import com.mays.srm.exception.InternalServerException;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeDao repository;
    private final DepartmentDao departmentDao;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public EmployeeServiceImpl(EmployeeDao repository, DepartmentDao departmentDao, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.departmentDao = departmentDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Employee create(Employee entity) {
        try {
            if (entity.getDepartment().getDepartmentId() != null) {

                Optional<Department> deptOpt = departmentDao.findById(entity.getDepartment().getDepartmentId());

                if (deptOpt.isPresent()) {
                    entity.setDepartment(deptOpt.get());
                } else {
                    throw new ResourceNotFoundException(
                            "Department not found with ID: " + entity.getDepartment().getDepartmentId()
                    );
                }
            }

            if (entity.getPassword() != null) {
                entity.setPassword(passwordEncoder.encode(entity.getPassword()));
            }

            return repository.save(entity);

        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (DataIntegrityViolationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while creating Employee", ex);
        }
    }

    @Override
    public Optional<Employee> getById(Integer id) {
        Optional<Employee> employee = repository.findById(id);
        if (employee.isEmpty()) {
            throw new ResourceNotFoundException("Employee not found with ID: " + id);
        }
        return employee;
    }

    @Override
    public List<Employee> getAll() {
        return repository.findAll();
    }

    @Override
    public Employee update(Employee entity) {
        try {
            if (entity.getDepartment().getDepartmentId() != null) {

                Optional<Department> deptOpt = departmentDao.findById(entity.getDepartment().getDepartmentId());

                if (deptOpt.isPresent()) {
                    entity.setDepartment(deptOpt.get());
                } else {
                    throw new ResourceNotFoundException(
                            "Department not found with ID: " + entity.getDepartment().getDepartmentId()
                    );
                }
            }

            if (entity.getPassword() != null && !entity.getPassword().startsWith("$2a$")) {
                entity.setPassword(passwordEncoder.encode(entity.getPassword()));
            }

            return repository.save(entity);

        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (DataIntegrityViolationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while updating Employee", ex);
        }
    }

    @Override
    public void delete(Integer id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete. Employee not found with ID: " + id);
        }
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
             throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while deleting Employee with ID: " + id, ex);
        }
    }

    // Existing find methods, added exception handling
    public Optional<Employee> findEmployeeByMobileNo(String mobileNo) {
        Optional<Employee> employee = repository.findByMobileNo(mobileNo);
        if (employee.isEmpty()) {
            throw new ResourceNotFoundException("Employee not found with mobile number: " + mobileNo);
        }
        return employee;
    }

    public Optional<Employee> findEmployeeByEmail(String email) {
        Optional<Employee> employee = repository.findByEmail(email);
        if (employee.isEmpty()) {
            throw new ResourceNotFoundException("Employee not found with email: " + email);
        }
        return employee;
    }

    public List<Employee> findByEmployeeName(String employeeName) {
        List<Employee> employees = repository.findByEmployeeName(employeeName);
        if (employees.isEmpty()) {
            throw new ResourceNotFoundException("No employees found with name: " + employeeName);
        }
        return employees;
    }

    public List<Employee> findEmployeeByDepartment(int departmentId) {
        List<Employee> employees = repository.findByDepartment(departmentId);
        if (employees.isEmpty()) {
            throw new ResourceNotFoundException("No employees found in department ID: " + departmentId);
        }
        return employees;
    }
}
