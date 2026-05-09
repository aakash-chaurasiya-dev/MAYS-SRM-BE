package com.mays.srm.service.impl;

import com.mays.srm.dao.core.BrandDao;
import com.mays.srm.dao.core.DeviceModelDao;
import com.mays.srm.entity.Brand;
import com.mays.srm.entity.DeviceModel;
import com.mays.srm.exception.BadRequestException;
import com.mays.srm.exception.InternalServerException;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.service.DeviceModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DeviceModelServiceImpl implements DeviceModelService {

    private final DeviceModelDao repository;
    private final BrandDao brandDao;

    @Autowired
    public DeviceModelServiceImpl(DeviceModelDao repository, BrandDao brandDao) {
        this.repository = repository;
        this.brandDao = brandDao;
    }

    @Override
    public DeviceModel create(DeviceModel entity) {
        try {
            if (entity.getBrand() != null) {
                Brand inputBrand = entity.getBrand();

                // Expecting existing brand using brandId
                if (inputBrand.getBrandId() != null) {
                    Optional<Brand> brandOpt = brandDao.findById(inputBrand.getBrandId());
                    if (brandOpt.isPresent()) {
                        entity.setBrand(brandOpt.get());
                    } else {
                        throw new ResourceNotFoundException("Brand not found with ID: " + inputBrand.getBrandId());
                    }
                } 
                else {
                    throw new BadRequestException("Valid Brand ID must be provided to create a DeviceModel.");
                }
            } else {
                 throw new BadRequestException("Brand information is required to create a DeviceModel.");
            }

            if (entity.getModelName() != null) {
                entity.setModelName(entity.getModelName().trim());
            } else {
                 throw new BadRequestException("Model Name is required.");
            }
            
            if (entity.getModelDescription() != null) {
                entity.setModelDescription(entity.getModelDescription().trim());
            }
            
            return repository.save(entity);
        } catch (ResourceNotFoundException | BadRequestException | DataIntegrityViolationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while creating DeviceModel", ex);
        }
    }

    @Override
    public Optional<DeviceModel> getById(Integer id) {
        Optional<DeviceModel> modelOpt = repository.findById(id);
        
        if (modelOpt.isPresent()) {
            return modelOpt;
        } else {
            throw new ResourceNotFoundException("DeviceModel not found with ID: " + id);
        }
    }

    @Override
    public List<DeviceModel> getAll() {
        return repository.findAll();
    }

    @Override
    public DeviceModel update(DeviceModel entity) {
        try {
            if (entity.getModelId() == null) {
                throw new ResourceNotFoundException("Cannot update. DeviceModel ID is missing.");
            }
            
            boolean exists = repository.existsById(entity.getModelId());
            if (!exists) {
                throw new ResourceNotFoundException("Cannot update. DeviceModel not found with ID: " + entity.getModelId());
            }

            if (entity.getBrand() != null) {
                Brand inputBrand = entity.getBrand();

                if (inputBrand.getBrandId() != null) {
                    Optional<Brand> brandOpt = brandDao.findById(inputBrand.getBrandId());
                    if (brandOpt.isPresent()) {
                        entity.setBrand(brandOpt.get());
                    } else {
                        throw new ResourceNotFoundException("Brand not found with ID: " + inputBrand.getBrandId());
                    }
                } else {
                    throw new BadRequestException("Valid Brand ID must be provided to update a DeviceModel.");
                }
            } else {
                 throw new BadRequestException("Brand information is required to update a DeviceModel.");
            }

            if (entity.getModelName() != null) {
                entity.setModelName(entity.getModelName().trim());
            } else {
                 throw new BadRequestException("Model Name is required.");
            }

            if (entity.getModelDescription() != null) {
                entity.setModelDescription(entity.getModelDescription().trim());
            }
            
            return repository.save(entity);
        } catch (ResourceNotFoundException | BadRequestException | DataIntegrityViolationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while updating DeviceModel", ex);
        }
    }

    @Override
    public void delete(Integer id) {
        boolean exists = repository.existsById(id);
        
        if (!exists) {
            throw new ResourceNotFoundException("Cannot delete. DeviceModel not found with ID: " + id);
        }
        
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
            throw new DataIntegrityViolationException("Cannot delete DeviceModel because it is assigned to existing Devices.", ex);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while deleting DeviceModel with ID: " + id, ex);
        }
    }

    @Override
    public Optional<DeviceModel> findByModelNameAndBrandName(String modelName, String brandName) {
        Optional<DeviceModel> modelOpt = repository.findByModelNameAndBrandName(modelName,brandName);
        if (modelOpt.isPresent()) {
            return Optional.of(modelOpt.get());
        } else {
            throw new ResourceNotFoundException("DeviceModel not found with model name: '" + modelName + "' and brand name: '" + brandName + "'");
        }
    }

    @Override
    public List<DeviceModel> findByModelName(String modelName) {
        List<DeviceModel> models = repository.findByModelName(modelName);
        if (models.isEmpty()){
            throw new ResourceNotFoundException("DeviceModel not found with model name: '" + modelName + "'");
        }
        return models;
    }
}
