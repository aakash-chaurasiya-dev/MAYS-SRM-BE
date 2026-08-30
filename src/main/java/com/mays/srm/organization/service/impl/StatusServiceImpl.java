package com.mays.srm.organization.service.impl;
import com.mays.srm.organization.repository.StatusDao;
import com.mays.srm.organization.dto.request.StatusRequestDTO;
import com.mays.srm.organization.dto.resDTO.StatusResponseDTO;
import com.mays.srm.organization.entities.Status;
import com.mays.srm.exception.InternalServerException;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.organization.service.StatusService;
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
public class StatusServiceImpl implements StatusService {

    private final StatusDao repository;
    private final ModelMapper modelMapper;
    private final DepartmentRoleResolver departmentRoleResolver;

    @Autowired
    public StatusServiceImpl(StatusDao repository, ModelMapper modelMapper,
                             DepartmentRoleResolver departmentRoleResolver) {
        this.repository = repository;
        this.modelMapper = modelMapper;
        this.departmentRoleResolver = departmentRoleResolver;
    }

    @Override
    @CacheEvict(value = "statuses", allEntries = true)
    public StatusResponseDTO create(StatusRequestDTO requestDTO) {
        try {
            Status status = modelMapper.map(requestDTO, Status.class);
            assignRoleFromDepartment(status);
            Status savedStatus = repository.save(status);
            return modelMapper.map(savedStatus, StatusResponseDTO.class);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while creating Status", ex);
        }
    }

    @Override
    @Cacheable(value = "statuses", key = "#id")
    public StatusResponseDTO getById(Integer id) {
        Optional<Status> statusOpt = repository.findById(id);
        if (statusOpt.isPresent()) {
            return modelMapper.map(statusOpt.get(), StatusResponseDTO.class);
        } else {
            throw new ResourceNotFoundException("Status not found with ID: " + id);
        }
    }

    @Override
    @Cacheable(value = "statuses", key = "'all'")
    public List<StatusResponseDTO> getAll() {
        List<Status> statusList = repository.findAll();
        List<StatusResponseDTO> dtoList = new ArrayList<>();
        for (Status status : statusList) {
            dtoList.add(modelMapper.map(status, StatusResponseDTO.class));
        }
        return dtoList;
    }

    @Override
    @CacheEvict(value = "statuses", allEntries = true)
    public StatusResponseDTO update(Integer id, StatusRequestDTO requestDTO) {
        Optional<Status> existingOpt = repository.findById(id);
        if (existingOpt.isEmpty()) {
            throw new ResourceNotFoundException("Cannot update. Status not found with ID: " + id);
        }
        if (Boolean.TRUE.equals(existingOpt.get().getIsLocked())) {
            throw new RuntimeException("Cannot modify a locked system configuration.");
        }
        Status existingStatus = existingOpt.get();
        modelMapper.map(requestDTO, existingStatus);

        try {
            assignRoleFromDepartment(existingStatus);
            Status updatedStatus = repository.save(existingStatus);
            return modelMapper.map(updatedStatus, StatusResponseDTO.class);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while updating Status", ex);
        }
    }

    @Override
    @CacheEvict(value = "statuses", allEntries = true)
    public void delete(Integer id) {

        var entityForLockCheck = repository.findById(id).orElseThrow(() -> new com.mays.srm.exception.ResourceNotFoundException("Not found with ID: " + id));
        if (Boolean.TRUE.equals(entityForLockCheck.getIsLocked())) {
            throw new RuntimeException("Cannot modify a locked system configuration.");
        }
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete. Status not found with ID: " + id);
        }
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
            throw new DataIntegrityViolationException("Cannot delete Status because it is currently in use.", ex);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while deleting Status with ID: " + id, ex);
        }
    }

    @Override
    @Cacheable(value = "statuses", key = "'type-' + #statusType")
    public List<StatusResponseDTO> getStatusesByType(String statusType) {
        List<Status> statuses = repository.getStatusesByType(statusType);
        List<StatusResponseDTO> dtoList = new java.util.ArrayList<>();
        for (Status status : statuses) {
            dtoList.add(modelMapper.map(status, StatusResponseDTO.class));
        }
        return dtoList;
    }

    private void assignRoleFromDepartment(Status status) {
        status.setAllowedRoles(departmentRoleResolver.resolveAllowedRolesCsv(status.getAllowedDepartmentIds()));
    }
}
