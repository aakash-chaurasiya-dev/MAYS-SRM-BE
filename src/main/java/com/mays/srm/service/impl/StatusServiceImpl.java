package com.mays.srm.service.impl;

import com.mays.srm.dao.core.StatusDao;
import com.mays.srm.entity.Status;
import com.mays.srm.exception.InternalServerException;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.service.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StatusServiceImpl implements StatusService {

    private final StatusDao repository;

    @Autowired
    public StatusServiceImpl(StatusDao repository) {
        this.repository = repository;
    }

    @Override
    public Status create(Status entity) {
        try {
            return repository.save(entity);
        } catch (DataIntegrityViolationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while creating Status", ex);
        }
    }

    @Override
    public Optional<Status> getById(Integer id) {
        Optional<Status> statusOpt = repository.findById(id);
        
        if (statusOpt.isPresent()) {
            return statusOpt;
        } else {
            throw new ResourceNotFoundException("Status not found with ID: " + id);
        }
    }

    @Override
    public List<Status> getAll() {
        return repository.findAll();
    }

    @Override
    public Status update(Status entity) {
        try {
            if (entity.getStatusId() == null) {
                throw new ResourceNotFoundException("Cannot update. Status ID is missing.");
            }
            
            boolean exists = repository.existsById(entity.getStatusId());
            if (exists == false) {
                throw new ResourceNotFoundException("Cannot update. Status not found with ID: " + entity.getStatusId());
            }
            
            return repository.save(entity);
            
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (DataIntegrityViolationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while updating Status", ex);
        }
    }

    @Override
    public void delete(Integer id) {
        boolean exists = repository.existsById(id);
        
        if (exists == false) {
            throw new ResourceNotFoundException("Cannot delete. Status not found with ID: " + id);
        }
        
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
             throw new DataIntegrityViolationException("Cannot delete Status because it is assigned to existing tickets or other records.", ex);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while deleting Status with ID: " + id, ex);
        }
    }
}
