package com.mays.srm.service.impl;

import com.mays.srm.dao.core.DeviceTypeDao;
import com.mays.srm.entity.DeviceType;
import com.mays.srm.exception.BadRequestException;
import com.mays.srm.exception.InternalServerException;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.service.DeviceTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DeviceTypeServiceImpl implements DeviceTypeService {

    private final DeviceTypeDao repository;

    @Autowired
    public DeviceTypeServiceImpl(DeviceTypeDao repository) {
        this.repository = repository;
    }

    @Override
    public DeviceType create(DeviceType entity) {
        try {
            if (entity.getDeviceTypeName() != null) {
                entity.setDeviceTypeName(entity.getDeviceTypeName().trim());
            } else {
                 throw new BadRequestException("DeviceType Name is required.");
            }
            
            if (entity.getDeviceTypeDescription() != null) {
                entity.setDeviceTypeDescription(entity.getDeviceTypeDescription().trim());
            }
            
            return repository.save(entity);
        } catch (BadRequestException | DataIntegrityViolationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while creating DeviceType", ex);
        }
    }

    @Override
    public Optional<DeviceType> getById(Integer id) {
        Optional<DeviceType> typeOpt = repository.findById(id);
        
        if (typeOpt.isPresent()) {
            return typeOpt;
        } else {
            throw new ResourceNotFoundException("DeviceType not found with ID: " + id);
        }
    }

    @Override
    public List<DeviceType> getAll() {
        return repository.findAll();
    }

    @Override
    public DeviceType update(DeviceType entity) {
        try {
            if (entity.getDeviceTypeId() == null) {
                throw new ResourceNotFoundException("Cannot update. DeviceType ID is missing.");
            }
            
            boolean exists = repository.existsById(entity.getDeviceTypeId());
            if (!exists) {
                throw new ResourceNotFoundException("Cannot update. DeviceType not found with ID: " + entity.getDeviceTypeId());
            }

            if (entity.getDeviceTypeName() != null) {
                entity.setDeviceTypeName(entity.getDeviceTypeName().trim());
            } else {
                 throw new BadRequestException("DeviceType Name is required.");
            }
            
            if (entity.getDeviceTypeDescription() != null) {
                entity.setDeviceTypeDescription(entity.getDeviceTypeDescription().trim());
            }
            
            return repository.save(entity);
        } catch (ResourceNotFoundException | BadRequestException | DataIntegrityViolationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while updating DeviceType", ex);
        }
    }

    @Override
    public void delete(Integer id) {
        boolean exists = repository.existsById(id);
        
        if (!exists) {
            throw new ResourceNotFoundException("Cannot delete. DeviceType not found with ID: " + id);
        }
        
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
            throw new DataIntegrityViolationException("Cannot delete DeviceType because it is assigned to existing Brands.", ex);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while deleting DeviceType with ID: " + id, ex);
        }
    }
}
