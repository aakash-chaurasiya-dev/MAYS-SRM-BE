package com.mays.srm.service.impl;

import com.mays.srm.dao.core.BrandDao;
import com.mays.srm.dao.core.EnquiryDao;
import com.mays.srm.dao.core.StatusDao;
import com.mays.srm.dao.core.UserMasterDao;
import com.mays.srm.entity.Brand;
import com.mays.srm.entity.Enquiry;
import com.mays.srm.entity.Status;
import com.mays.srm.entity.UserMaster;
import com.mays.srm.exception.BadRequestException;
import com.mays.srm.exception.InternalServerException;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.service.EnquiryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EnquiryServiceImpl implements EnquiryService {

    private final EnquiryDao repository;
    private final StatusDao statusDao;
    private final UserMasterDao userMasterDao;
    private final BrandDao brandDao;

    @Autowired
    public EnquiryServiceImpl(EnquiryDao repository, StatusDao statusDao, UserMasterDao userMasterDao, BrandDao brandDao) {
        this.repository = repository;
        this.statusDao = statusDao;
        this.userMasterDao = userMasterDao;
        this.brandDao = brandDao;
    }

    @Override
    public Enquiry create(Enquiry entity) {
        try {
            validateAndSetRelations(entity);
            
            // Automatically set the timestamp for new enquiries if not provided
            if (entity.getTimestamp() == null) {
                entity.setTimestamp(LocalDateTime.now());
            }

            return repository.save(entity);
        } catch (ResourceNotFoundException | BadRequestException | DataIntegrityViolationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while creating Enquiry", ex);
        }
    }

    @Override
    public Optional<Enquiry> getById(Integer id) {
        Optional<Enquiry> enquiryOpt = repository.findById(id);
        
        if (enquiryOpt.isPresent()) {
            return enquiryOpt;
        } else {
            throw new ResourceNotFoundException("Enquiry not found with ID: " + id);
        }
    }

    @Override
    public List<Enquiry> getAll() {
        return repository.findAll();
    }

    @Override
    public Enquiry update(Enquiry entity) {
        try {
            if (entity.getEnquiryId() == null) {
                throw new ResourceNotFoundException("Cannot update. Enquiry ID is missing.");
            }
            
            boolean exists = repository.existsById(entity.getEnquiryId());
            if (!exists) {
                throw new ResourceNotFoundException("Cannot update. Enquiry not found with ID: " + entity.getEnquiryId());
            }

            validateAndSetRelations(entity);

            return repository.save(entity);
        } catch (ResourceNotFoundException | BadRequestException | DataIntegrityViolationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while updating Enquiry", ex);
        }
    }

    @Override
    public void delete(Integer id) {
        boolean exists = repository.existsById(id);
        if (!exists) {
            throw new ResourceNotFoundException("Cannot delete. Enquiry not found with ID: " + id);
        }
        
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
            throw new DataIntegrityViolationException("Cannot delete Enquiry due to database constraints.", ex);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while deleting Enquiry with ID: " + id, ex);
        }
    }

    /**
     * Helper method to validate and set User, Brand, and Status relations
     */
    private void validateAndSetRelations(Enquiry entity) {
        // Validate User
        if (entity.getUser() != null && entity.getUser().getUserId() != null) {
            Optional<UserMaster> userOpt = userMasterDao.findById(entity.getUser().getUserId());
            if (userOpt.isPresent()) {
                entity.setUser(userOpt.get());
            } else {
                throw new ResourceNotFoundException("User not found with ID: " + entity.getUser().getUserId());
            }
        } else {
             // Enquiries might be allowed without a user? If a User is MANDATORY, uncomment the throw below:
              throw new BadRequestException("User ID is required to create/update an Enquiry.");
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

        // Validate Status
        if (entity.getStatus() != null && entity.getStatus().getStatusId() != null) {
            Optional<Status> statusOpt = statusDao.findById(entity.getStatus().getStatusId());
            if (statusOpt.isPresent()) {
                Status dbStatus = statusOpt.get();
                // Ensure the status is specifically meant for an "enquiry"
                if (!"enquiry".equalsIgnoreCase(dbStatus.getStatusType())) {
                    throw new BadRequestException("Status must be of type 'enquiry'");
                }
                entity.setStatus(dbStatus);
            } else {
                throw new ResourceNotFoundException("Status not found with ID: " + entity.getStatus().getStatusId());
            }
        }
    }
}
