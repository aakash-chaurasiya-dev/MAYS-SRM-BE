package com.mays.srm.service.impl;

import com.mays.srm.dao.core.BrandDao;
import com.mays.srm.dao.core.DeviceTypeDao;
import com.mays.srm.entity.Brand;
import com.mays.srm.entity.DeviceType;
import com.mays.srm.exception.BadRequestException;
import com.mays.srm.exception.InternalServerException;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BrandServiceImpl implements BrandService {

    private final BrandDao repository;
    private final DeviceTypeDao deviceTypeDao;

    @Autowired
    public BrandServiceImpl(BrandDao repository, DeviceTypeDao deviceTypeDao) {
        this.repository = repository;
        this.deviceTypeDao = deviceTypeDao;
    }

    @Override
    public Brand create(Brand entity) {
        try {
            if (entity.getDeviceType() != null && entity.getDeviceType().getDeviceTypeId() != null) {
                Optional<DeviceType> dtOpt = deviceTypeDao.findById(entity.getDeviceType().getDeviceTypeId());
                if (dtOpt.isPresent()) {
                    entity.setDeviceType(dtOpt.get());
                } else {
                    throw new ResourceNotFoundException("DeviceType not found with ID: " + entity.getDeviceType().getDeviceTypeId());
                }
            } else {
                throw new BadRequestException("DeviceType with ID is required to create a new Brand.");
            }
            
            if(entity.getBrandName() != null) {
                entity.setBrandName(entity.getBrandName().trim());
            } else {
                throw new BadRequestException("Brand Name is required.");
            }

            if(entity.getBrandDescription() != null) {
                entity.setBrandDescription(entity.getBrandDescription().trim());
            }
            
            return repository.save(entity);
        } catch (ResourceNotFoundException | BadRequestException | DataIntegrityViolationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while creating Brand", ex);
        }
    }

    @Override
    public Optional<Brand> getById(Integer id) {
        Optional<Brand> brandOpt = repository.findById(id);
        if (brandOpt.isPresent()) {
            return brandOpt;
        } else {
            throw new ResourceNotFoundException("Brand not found with ID: " + id);
        }
    }

    @Override
    public List<Brand> getAll() {
        return repository.findAll();
    }

    @Override
    public Brand update(Brand entity) {
        try {
            if (entity.getBrandId() == null) {
                throw new ResourceNotFoundException("Cannot update. Brand ID is missing.");
            }
            
            boolean exists = repository.existsById(entity.getBrandId());
            if (!exists) {
                throw new ResourceNotFoundException("Cannot update. Brand not found with ID: " + entity.getBrandId());
            }

            if (entity.getDeviceType() != null && entity.getDeviceType().getDeviceTypeId() != null) {
                Optional<DeviceType> dtOpt = deviceTypeDao.findById(entity.getDeviceType().getDeviceTypeId());
                if (dtOpt.isPresent()) {
                    entity.setDeviceType(dtOpt.get());
                } else {
                    throw new ResourceNotFoundException("DeviceType not found with ID: " + entity.getDeviceType().getDeviceTypeId());
                }
            } else {
                throw new BadRequestException("DeviceType with ID is required to update a Brand.");
            }
            
            if(entity.getBrandName() != null) {
                entity.setBrandName(entity.getBrandName().trim());
            } else {
                throw new BadRequestException("Brand Name is required.");
            }

            if(entity.getBrandDescription() != null) {
                entity.setBrandDescription(entity.getBrandDescription().trim());
            }
            
            return repository.save(entity);
        } catch (ResourceNotFoundException | BadRequestException | DataIntegrityViolationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while updating Brand", ex);
        }
    }

    @Override
    public void delete(Integer id) {
        boolean exists = repository.existsById(id);
        
        if (!exists) {
            throw new ResourceNotFoundException("Cannot delete. Brand not found with ID: " + id);
        }
        
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
             throw new DataIntegrityViolationException("Cannot delete Brand because it is assigned to existing Device Models.", ex);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while deleting Brand with ID: " + id, ex);
        }
    }
}
