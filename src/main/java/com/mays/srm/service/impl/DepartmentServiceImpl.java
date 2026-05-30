package com.mays.srm.service.impl;

import com.mays.srm.dao.core.DepartmentDao;
import com.mays.srm.dto.requestDTO.DepartmentRequestDTO;
import com.mays.srm.dto.responseDTO.DepartmentResponseDTO;
import com.mays.srm.entity.Department;
import com.mays.srm.exception.InternalServerException;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.service.DepartmentService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentDao repository;
    private final ModelMapper modelMapper;

    @Autowired
    public DepartmentServiceImpl(DepartmentDao repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
    }

    @Override
    public DepartmentResponseDTO create(DepartmentRequestDTO requestDTO) {
        try {
            Department department = modelMapper.map(requestDTO, Department.class);
            Department savedDepartment = repository.save(department);
            return modelMapper.map(savedDepartment, DepartmentResponseDTO.class);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while creating Department", ex);
        }
    }

    @Override
    public DepartmentResponseDTO getById(Integer id) {
        Optional<Department> departmentOpt = repository.findById(id);
        if (departmentOpt.isPresent()) {
            return modelMapper.map(departmentOpt.get(), DepartmentResponseDTO.class);
        } else {
            throw new ResourceNotFoundException("Department not found with ID: " + id);
        }
    }

    @Override
    public List<DepartmentResponseDTO> getAll() {
        List<Department> departmentList = repository.findAll();
        List<DepartmentResponseDTO> dtoList = new ArrayList<>();
        for (Department department : departmentList) {
            dtoList.add(modelMapper.map(department, DepartmentResponseDTO.class));
        }
        return dtoList;
    }

    @Override
    public DepartmentResponseDTO update(Integer id, DepartmentRequestDTO requestDTO) {
        Optional<Department> existingOpt = repository.findById(id);
        if (existingOpt.isEmpty()) {
            throw new ResourceNotFoundException("Cannot update. Department not found with ID: " + id);
        }
        
        Department existingDepartment = existingOpt.get();
        modelMapper.map(requestDTO, existingDepartment);
        
        try {
            Department updatedDepartment = repository.save(existingDepartment);
            return modelMapper.map(updatedDepartment, DepartmentResponseDTO.class);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while updating Department", ex);
        }
    }

    @Override
    public void delete(Integer id) {
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
