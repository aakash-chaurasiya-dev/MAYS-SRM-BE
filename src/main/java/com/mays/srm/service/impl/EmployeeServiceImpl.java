package com.mays.srm.service.impl;

import com.mays.srm.dao.core.DepartmentDao;
import com.mays.srm.dao.core.EmployeeDao;
import com.mays.srm.dao.core.EmployeeSpecDao; // Keep this import for now, but it will be removed
import com.mays.srm.dao.core.UserMasterDao;
import com.mays.srm.dto.requestDTO.EmployeeRequestDTO;
import com.mays.srm.dto.responseDTO.EmployeeResponseDTO;
import com.mays.srm.entity.Department;
import com.mays.srm.entity.Employee;
import com.mays.srm.entity.UserMaster;
import com.mays.srm.exception.BadRequestException;
import com.mays.srm.exception.InternalServerException;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.service.EmployeeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeDao repository;
    private final DepartmentDao departmentDao;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final UserMasterDao userMasterDao;

    // EmployeeSpecDao is no longer needed for explicit deletion due to cascade
    // private final EmployeeSpecDao employeeSpecDao;

    @Autowired
    public EmployeeServiceImpl(EmployeeDao repository, DepartmentDao departmentDao, PasswordEncoder passwordEncoder, ModelMapper modelMapper, UserMasterDao userMasterDao) {
        this.repository = repository;
        this.departmentDao = departmentDao;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.userMasterDao = userMasterDao;
    }

    @Override
    public EmployeeResponseDTO create(EmployeeRequestDTO requestDTO) {
        try {
            validateMobileNumber(requestDTO.getMobileNo(), null, null);
            Employee employee = modelMapper.map(requestDTO, Employee.class);

            validateAndSetRelations(employee, requestDTO);
            assignRoleBasedOnDepartment(employee);

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
        return employeeList.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeResponseDTO update(Integer id, EmployeeRequestDTO requestDTO) {
        Optional<Employee> existingOpt = repository.findById(id);
        if (existingOpt.isEmpty()) {
            throw new ResourceNotFoundException("Cannot update. Employee not found with ID: " + id);
        }

        validateMobileNumber(requestDTO.getMobileNo(), id, null);

        Employee existingEmployee = existingOpt.get();
        String currentPassword = existingEmployee.getPassword();
        
        modelMapper.map(requestDTO, existingEmployee);
        existingEmployee.setEmployeeId(id);

        if (requestDTO.getPassword() == null || requestDTO.getPassword().isEmpty()) {
            existingEmployee.setPassword(currentPassword);
        } else if (!requestDTO.getPassword().startsWith("$2a$")) {
            existingEmployee.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        }

        validateAndSetRelations(existingEmployee, requestDTO);
        assignRoleBasedOnDepartment(existingEmployee);

        try {
            Employee updatedEmployee = repository.save(existingEmployee);
            return mapToResponseDTO(updatedEmployee);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while updating Employee", ex);
        }
    }

    @Override
    @Transactional // Ensure this is transactional
    public void delete(Integer id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete. Employee not found with ID: " + id);
        }
        try {
            // Hibernate will now handle cascading deletion of EmployeeSpec due to CascadeType.ALL
            repository.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
            throw new DataIntegrityViolationException("Cannot delete Employee because they are currently assigned to active records (e.g., Tickets).", ex);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while deleting Employee with ID: " + id, ex);
        }
    }

    @Override
    @Transactional // Ensure this is transactional
    public void deleteEmployees(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("List of IDs to delete cannot be empty");
        }
        try {
            // First check if all exist to provide a better error message if needed
            List<Employee> employeesToDelete = repository.findAllById(ids);
            if (employeesToDelete.size() != ids.size()) {
                throw new ResourceNotFoundException("One or more Employees not found for deletion.");
            }
            // Hibernate will now handle cascading deletion of EmployeeSpec due to CascadeType.ALL
            repository.deleteAllByIdInBatch(ids); // Efficiently deletes multiple records
        } catch (DataIntegrityViolationException ex) {
             throw new DataIntegrityViolationException("Cannot delete one or more Employees because they are currently assigned to active records (e.g., Tickets).", ex);
        } catch (Exception ex) {
             throw new InternalServerException("Error occurred while deleting multiple Employees", ex);
        }
    }

    @Override
    public List<EmployeeResponseDTO> getEmployeesByDepartmentId(Integer departmentId) {
        if (!departmentDao.existsById(departmentId)) {
            throw new ResourceNotFoundException("Department not found with ID: " + departmentId);
        }
        
        List<Employee> employees = repository.findByDepartmentDepartmentId(departmentId);
        return employees.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void validateMobileNumber(String mobileNo, Integer currentEmployeeId, Integer currentUserId) {
        if (mobileNo == null || mobileNo.trim().isEmpty()) {
            return;
        }

        // Check Employee table
        Optional<Employee> employeeOpt = repository.findByMobileNo(mobileNo);
        if (employeeOpt.isPresent()) {
            if (currentEmployeeId == null || !employeeOpt.get().getEmployeeId().equals(currentEmployeeId)) {
                throw new BadRequestException("Mobile number is already registered as an Employee.");
            }
        }

        // Check User table
        Optional<UserMaster> userOpt = userMasterDao.findByMobileNo(mobileNo);
        if (userOpt.isPresent()) {
            if (currentUserId == null || !userOpt.get().getUserId().equals(currentUserId)) {
                throw new BadRequestException("Mobile number is already registered as a User.");
            }
        }
    }

    private void validateAndSetRelations(Employee employee, EmployeeRequestDTO requestDTO) {
        if (requestDTO.getDepartmentId() != null) {
            Optional<Department> departmentOpt = departmentDao.findById(requestDTO.getDepartmentId());
            if (departmentOpt.isPresent()) {
                employee.setDepartment(departmentOpt.get());
            } else {
                throw new ResourceNotFoundException("Department not found with ID: " + requestDTO.getDepartmentId());
            }
        } else {
            employee.setDepartment(null);
        }
    }

    private void assignRoleBasedOnDepartment(Employee employee) {
        if (employee.getDepartment() != null) {
            String departmentName = employee.getDepartment().getDepartmentName();
            
            if ("Engineer".equalsIgnoreCase(departmentName)) {
                employee.setRole("ROLE_ENGINEER");
            } else if ("Purchase Team".equalsIgnoreCase(departmentName)) {
                employee.setRole("ROLE_PURCHASE");
            } else if ("Management".equalsIgnoreCase(departmentName)) {
                employee.setRole("ROLE_MANAGER");
            } else {
                employee.setRole("ROLE_ADMIN");
            }
        } else {
            throw new ResourceNotFoundException("Department not found with ID: " + employee.getDepartment().getDepartmentId());
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
