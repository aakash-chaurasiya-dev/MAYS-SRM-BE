package com.mays.srm.service.impl;

import com.mays.srm.dao.core.DepartmentDao;
import com.mays.srm.dao.core.EmployeeDao;
import com.mays.srm.dto.requestDTO.EmployeeRequestDTO;
import com.mays.srm.dto.responseDTO.EmployeeResponseDTO;
import com.mays.srm.entity.Department;
import com.mays.srm.entity.Employee;
import com.mays.srm.exception.InternalServerException;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.service.EmployeeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeDao repository;
    private final DepartmentDao departmentDao;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Autowired
    public EmployeeServiceImpl(EmployeeDao repository, DepartmentDao departmentDao, PasswordEncoder passwordEncoder, ModelMapper modelMapper) {
        this.repository = repository;
        this.departmentDao = departmentDao;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    @Override
    public EmployeeResponseDTO create(EmployeeRequestDTO requestDTO) {
        try {
            Employee employee = modelMapper.map(requestDTO, Employee.class);

            if (requestDTO.getDepartmentId() != null) {
                Optional<Department> departmentOpt = departmentDao.findById(requestDTO.getDepartmentId());
                if (departmentOpt.isPresent()) {
                    employee.setDepartment(departmentOpt.get());
                } else {
                    throw new ResourceNotFoundException("Department not found with ID: " + requestDTO.getDepartmentId());
                }
            }

            if (employee.getPassword() != null) {
                employee.setPassword(passwordEncoder.encode(employee.getPassword()));
            }

            Employee savedEmployee = repository.save(employee);
            return mapToResponseDTO(savedEmployee);
        } catch (ResourceNotFoundException | DataIntegrityViolationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while creating Employee", ex);
        }
    }

    @Override
    public EmployeeResponseDTO getById(Integer id) {
        Optional<Employee> employeeOpt = repository.findById(id);
        if (employeeOpt.isPresent()) {
            return mapToResponseDTO(employeeOpt.get());
        } else {
            throw new ResourceNotFoundException("Employee not found with ID: " + id);
        }
    }

    @Override
    public List<EmployeeResponseDTO> getAll() {
        List<Employee> employeeList = repository.findAll();
        List<EmployeeResponseDTO> dtoList = new ArrayList<>();
        for (Employee employee : employeeList) {
            dtoList.add(mapToResponseDTO(employee));
        }
        return dtoList;
    }

    @Override
    public EmployeeResponseDTO update(Integer id, EmployeeRequestDTO requestDTO) {
        Optional<Employee> existingOpt = repository.findById(id);
        if (existingOpt.isEmpty()) {
            throw new ResourceNotFoundException("Cannot update. Employee not found with ID: " + id);
        }

        Employee existingEmployee = existingOpt.get();
        String currentPassword = existingEmployee.getPassword();
        
        modelMapper.map(requestDTO, existingEmployee);
        existingEmployee.setEmployeeId(id);

        if (requestDTO.getPassword() == null || requestDTO.getPassword().isEmpty()) {
            existingEmployee.setPassword(currentPassword);
        } else if (!requestDTO.getPassword().startsWith("$2a$")) {
            existingEmployee.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        }

        if (requestDTO.getDepartmentId() != null) {
            Optional<Department> departmentOpt = departmentDao.findById(requestDTO.getDepartmentId());
            if (departmentOpt.isPresent()) {
                existingEmployee.setDepartment(departmentOpt.get());
            } else {
                throw new ResourceNotFoundException("Department not found with ID: " + requestDTO.getDepartmentId());
            }
        } else {
            existingEmployee.setDepartment(null);
        }

        try {
            Employee updatedEmployee = repository.save(existingEmployee);
            return mapToResponseDTO(updatedEmployee);
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
            throw new DataIntegrityViolationException("Cannot delete Employee because they are currently assigned to active records.", ex);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while deleting Employee with ID: " + id, ex);
        }
    }

    private EmployeeResponseDTO mapToResponseDTO(Employee employee) {
        EmployeeResponseDTO dto = modelMapper.map(employee, EmployeeResponseDTO.class);
        if (employee.getDepartment() != null) {
            dto.setDepartmentName(employee.getDepartment().getDepartmentName());
        }
        return dto;
    }
}
