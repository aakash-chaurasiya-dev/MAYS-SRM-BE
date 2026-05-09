package com.mays.srm.service.impl;

import com.mays.srm.dao.core.BranchDao;
import com.mays.srm.dao.core.BrandDao;
import com.mays.srm.dao.core.DeviceTypeDao;
import com.mays.srm.dao.core.InventoryDao;
import com.mays.srm.entity.Branch;
import com.mays.srm.entity.Brand;
import com.mays.srm.entity.DeviceType;
import com.mays.srm.entity.Inventory;
import com.mays.srm.exception.BadRequestException;
import com.mays.srm.exception.InternalServerException;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryDao repository;
    private final DeviceTypeDao deviceTypeDao;
    private final BrandDao brandDao;
    private final BranchDao branchDao;

    @Autowired
    public InventoryServiceImpl(InventoryDao repository, DeviceTypeDao deviceTypeDao, BrandDao brandDao, BranchDao branchDao) {
        this.repository = repository;
        this.deviceTypeDao = deviceTypeDao;
        this.brandDao = brandDao;
        this.branchDao = branchDao;
    }

    @Override
    public Inventory create(Inventory entity) {
        try {
            if (entity.getProductName() == null || entity.getProductName().trim().isEmpty()) {
                throw new BadRequestException("Product name is required.");
            }
            
            validateAndSetRelations(entity);

            return repository.save(entity);
        } catch (ResourceNotFoundException | BadRequestException | DataIntegrityViolationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while creating Inventory record", ex);
        }
    }

    @Override
    public Optional<Inventory> getById(Integer id) {
        Optional<Inventory> inventoryOpt = repository.findById(id);
        if (inventoryOpt.isPresent()) {
            return inventoryOpt;
        } else {
            throw new ResourceNotFoundException("Inventory record not found with ID: " + id);
        }
    }

    @Override
    public List<Inventory> getAll() {
        return repository.findAll();
    }

    @Override
    public Inventory update(Inventory entity) {
        try {
            if (entity.getProductId() == null) {
                throw new ResourceNotFoundException("Cannot update. Inventory ID is missing.");
            }
            
            boolean exists = repository.existsById(entity.getProductId());
            if (!exists) {
                throw new ResourceNotFoundException("Cannot update. Inventory record not found with ID: " + entity.getProductId());
            }

            if (entity.getProductName() == null || entity.getProductName().trim().isEmpty()) {
                throw new BadRequestException("Product name is required.");
            }

            validateAndSetRelations(entity);

            return repository.save(entity);
        } catch (ResourceNotFoundException | BadRequestException | DataIntegrityViolationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while updating Inventory record", ex);
        }
    }

    @Override
    public void delete(Integer id) {
        boolean exists = repository.existsById(id);
        if (!exists) {
            throw new ResourceNotFoundException("Cannot delete. Inventory record not found with ID: " + id);
        }
        
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
            throw new DataIntegrityViolationException("Cannot delete Inventory record because it is currently assigned to a Billing record or Ticket.", ex);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while deleting Inventory record with ID: " + id, ex);
        }
    }

    /**
     * Helper method to validate and set DeviceType, Brand, and Branch relations
     */
    private void validateAndSetRelations(Inventory entity) {
        // Validate DeviceType
        if (entity.getDeviceType() != null && entity.getDeviceType().getDeviceTypeId() != null) {
            Optional<DeviceType> dtOpt = deviceTypeDao.findById(entity.getDeviceType().getDeviceTypeId());
            if (dtOpt.isPresent()) {
                entity.setDeviceType(dtOpt.get());
            } else {
                throw new ResourceNotFoundException("DeviceType not found with ID: " + entity.getDeviceType().getDeviceTypeId());
            }
        }

        // Validate Brand
        if (entity.getBrand() != null && entity.getBrand().getBrandId() != null) {
            Optional<Brand> brandOpt = brandDao.findById(entity.getBrand().getBrandId());
            if (brandOpt.isPresent()) {
                entity.setBrand(brandOpt.get());
            } else {
                throw new ResourceNotFoundException("Brand not found with ID: " + entity.getBrand().getBrandId());
            }
        }

        // Validate Branch
        if (entity.getBranch() != null && entity.getBranch().getBranchId() != null) {
            Optional<Branch> branchOpt = branchDao.findById(entity.getBranch().getBranchId());
            if (branchOpt.isPresent()) {
                entity.setBranch(branchOpt.get());
            } else {
                throw new ResourceNotFoundException("Branch not found with ID: " + entity.getBranch().getBranchId());
            }
        }
    }
}
