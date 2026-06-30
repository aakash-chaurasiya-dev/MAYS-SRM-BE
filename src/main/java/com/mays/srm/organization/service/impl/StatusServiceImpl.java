package com.mays.srm.organization.service.impl;
import com.mays.srm.organization.repository.StatusDao;
import com.mays.srm.organization.dto.request.StatusRequestDTO;
import com.mays.srm.organization.dto.resDTO.StatusResponseDTO;
import com.mays.srm.organization.entities.Status;
import com.mays.srm.exception.InternalServerException;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.organization.service.StatusService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StatusServiceImpl implements StatusService {

    private final StatusDao repository;
    private final ModelMapper modelMapper;

    @Autowired
    public StatusServiceImpl(StatusDao repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
    }

    @Override
    public StatusResponseDTO create(StatusRequestDTO requestDTO) {
        try {
            Status status = modelMapper.map(requestDTO, Status.class);
            Status savedStatus = repository.save(status);
            return modelMapper.map(savedStatus, StatusResponseDTO.class);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while creating Status", ex);
        }
    }

    @Override
    public StatusResponseDTO getById(Integer id) {
        Optional<Status> statusOpt = repository.findById(id);
        if (statusOpt.isPresent()) {
            return modelMapper.map(statusOpt.get(), StatusResponseDTO.class);
        } else {
            throw new ResourceNotFoundException("Status not found with ID: " + id);
        }
    }

    @Override
    public List<StatusResponseDTO> getAll() {
        List<Status> statusList = repository.findAll();
        List<StatusResponseDTO> dtoList = new ArrayList<>();
        for (Status status : statusList) {
            dtoList.add(modelMapper.map(status, StatusResponseDTO.class));
        }
        return dtoList;
    }

    @Override
    public StatusResponseDTO update(Integer id, StatusRequestDTO requestDTO) {
        Optional<Status> existingOpt = repository.findById(id);
        if (existingOpt.isEmpty()) {
            throw new ResourceNotFoundException("Cannot update. Status not found with ID: " + id);
        }
        
        Status existingStatus = existingOpt.get();
        modelMapper.map(requestDTO, existingStatus);
        
        try {
            Status updatedStatus = repository.save(existingStatus);
            return modelMapper.map(updatedStatus, StatusResponseDTO.class);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while updating Status", ex);
        }
    }

    @Override
    public void delete(Integer id) {
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
    public List<StatusResponseDTO> getStatusesByType(String statusType) {
        List<Status> statuses = repository.getStatusesByType(statusType);
        List<StatusResponseDTO> dtoList = new java.util.ArrayList<>();
        for (Status status : statuses) {
            dtoList.add(modelMapper.map(status, StatusResponseDTO.class));
        }
        return dtoList;
    }
}

