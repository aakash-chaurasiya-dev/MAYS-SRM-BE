package com.mays.srm.organization.service.impl;
import com.mays.srm.organization.repository.DepartmentDao;
import com.mays.srm.organization.dto.request.DepartmentRequestDTO;
import com.mays.srm.organization.dto.resDTO.DepartmentResponseDTO;
import com.mays.srm.organization.entities.Department;
import com.mays.srm.exception.BadRequestException;
import com.mays.srm.exception.InternalServerException;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.organization.service.DepartmentService;
import com.mays.srm.organization.util.DepartmentRoleResolver;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentDao repository;
    private final ModelMapper modelMapper;
    private final DepartmentRoleResolver departmentRoleResolver;

    @Autowired
    public DepartmentServiceImpl(DepartmentDao repository, ModelMapper modelMapper,
                                 DepartmentRoleResolver departmentRoleResolver) {
        this.repository = repository;
        this.modelMapper = modelMapper;
        this.departmentRoleResolver = departmentRoleResolver;
    }

    @Override
    @CacheEvict(value = "departments", allEntries = true)
    public DepartmentResponseDTO create(DepartmentRequestDTO requestDTO) {
        try {
            departmentRoleResolver.validateKnownRole(requestDTO.getDefaultRole());
            Department department = modelMapper.map(requestDTO, Department.class);
            department.setDefaultRole(requestDTO.getDefaultRole().trim());
            Department savedDepartment = repository.save(department);
            return modelMapper.map(savedDepartment, DepartmentResponseDTO.class);
        } catch (BadRequestException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while creating Department", ex);
        }
    }

    @Override
    @Cacheable(value = "departments", key = "#id")
    public DepartmentResponseDTO getById(Integer id) {
        Optional<Department> departmentOpt = repository.findById(id);
        if (departmentOpt.isPresent()) {
            return modelMapper.map(departmentOpt.get(), DepartmentResponseDTO.class);
        } else {
            throw new ResourceNotFoundException("Department not found with ID: " + id);
        }
    }

    @Override
    @Cacheable(value = "departments", key = "'all'")
    public List<DepartmentResponseDTO> getAll() {
        List<Department> departmentList = repository.findAll();
        List<DepartmentResponseDTO> dtoList = new ArrayList<>();
        for (Department department : departmentList) {
            dtoList.add(modelMapper.map(department, DepartmentResponseDTO.class));
        }
        return dtoList;
    }

    @Override
    @CacheEvict(value = "departments", allEntries = true)
    public DepartmentResponseDTO update(Integer id, DepartmentRequestDTO requestDTO) {
        Optional<Department> existingOpt = repository.findById(id);
        if (existingOpt.isEmpty()) {
            throw new ResourceNotFoundException("Cannot update. Department not found with ID: " + id);
        }

        departmentRoleResolver.validateKnownRole(requestDTO.getDefaultRole());

        Department existingDepartment = existingOpt.get();
        modelMapper.map(requestDTO, existingDepartment);
        existingDepartment.setDefaultRole(requestDTO.getDefaultRole().trim());

        try {
            Department updatedDepartment = repository.save(existingDepartment);
            return modelMapper.map(updatedDepartment, DepartmentResponseDTO.class);
        } catch (BadRequestException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while updating Department", ex);
        }
    }

    @Override
    @CacheEvict(value = "departments", allEntries = true)
    public void delete(Integer id) {

        var entityForLockCheck = repository.findById(id).orElseThrow(() -> new com.mays.srm.exception.ResourceNotFoundException("Not found with ID: " + id));
        if (Boolean.TRUE.equals(entityForLockCheck.getIsLocked())) {
            throw new RuntimeException("Cannot modify a locked system configuration.");
        }
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete. Department not found with ID: " + id);
        }
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
            throw new DataIntegrityViolationException("Cannot delete Department because it is currently in use by an Employee.", ex);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while deleting Department with ID: " + id, ex);
        }
    }
}
