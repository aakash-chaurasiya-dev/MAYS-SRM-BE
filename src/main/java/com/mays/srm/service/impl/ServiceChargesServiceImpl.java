package com.mays.srm.service.impl;

import com.mays.srm.dao.core.BrandDao;
import com.mays.srm.dao.core.ServiceChargesDao;
import com.mays.srm.entity.Brand;
import com.mays.srm.entity.ServiceCharges;
import com.mays.srm.exception.BadRequestException;
import com.mays.srm.exception.InternalServerException;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.service.ServiceChargesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServiceChargesServiceImpl implements ServiceChargesService {

    private final ServiceChargesDao repository;
    private final BrandDao brandDao;

    @Autowired
    public ServiceChargesServiceImpl(ServiceChargesDao repository, BrandDao brandDao) {
        this.repository = repository;
        this.brandDao = brandDao;
    }

    @Override
    public ServiceCharges create(ServiceCharges entity) {
        try {
            validateAndSetRelations(entity);
            return repository.save(entity);
        } catch (ResourceNotFoundException | BadRequestException | DataIntegrityViolationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while creating ServiceCharges", ex);
        }
    }

    @Override
    public Optional<ServiceCharges> getById(Integer id) {
        Optional<ServiceCharges> chargesOpt = repository.findById(id);
        
        if (chargesOpt.isPresent()) {
            return chargesOpt;
        } else {
            throw new ResourceNotFoundException("ServiceCharges not found with ID: " + id);
        }
    }

    @Override
    public List<ServiceCharges> getAll() {
        return repository.findAll();
    }

    @Override
    public ServiceCharges update(ServiceCharges entity) {
        try {
            if (entity.getChargeId() == null) {
                throw new ResourceNotFoundException("Cannot update. ServiceCharges ID is missing.");
            }
            
            boolean exists = repository.existsById(entity.getChargeId());
            if (!exists) {
                throw new ResourceNotFoundException("Cannot update. ServiceCharges not found with ID: " + entity.getChargeId());
            }

            validateAndSetRelations(entity);

            return repository.save(entity);
        } catch (ResourceNotFoundException | BadRequestException | DataIntegrityViolationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while updating ServiceCharges", ex);
        }
    }

    @Override
    public void delete(Integer id) {
        boolean exists = repository.existsById(id);
        
        if (!exists) {
            throw new ResourceNotFoundException("Cannot delete. ServiceCharges not found with ID: " + id);
        }
        
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
            throw new DataIntegrityViolationException("Cannot delete ServiceCharges because it is assigned to existing Billing records.", ex);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while deleting ServiceCharges with ID: " + id, ex);
        }
    }

    /**
     * Helper method to validate and set Brand relations
     */
    private void validateAndSetRelations(ServiceCharges entity) {
        // Validate Brand
        if (entity.getBrand() != null && entity.getBrand().getBrandId() != null) {
            Optional<Brand> brandOpt = brandDao.findById(entity.getBrand().getBrandId());
            if (brandOpt.isPresent()) {
                entity.setBrand(brandOpt.get());
            } else {
                throw new ResourceNotFoundException("Brand not found with ID: " + entity.getBrand().getBrandId());
            }
        }
    }
}
