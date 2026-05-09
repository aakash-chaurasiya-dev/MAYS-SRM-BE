package com.mays.srm.service.impl;

import com.mays.srm.dao.core.TicketTypeDao;
import com.mays.srm.entity.TicketType;
import com.mays.srm.exception.InternalServerException;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.service.TicketTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TicketTypeServiceImpl implements TicketTypeService {

    private final TicketTypeDao repository;

    @Autowired
    public TicketTypeServiceImpl(TicketTypeDao repository) {
        this.repository = repository;
    }

    @Override
    public TicketType create(TicketType entity) {
        try {
            return repository.save(entity);
        } catch (DataIntegrityViolationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while creating TicketType", ex);
        }
    }

    @Override
    public Optional<TicketType> getById(Integer id) {
        Optional<TicketType> ticketTypeOpt = repository.findById(id);
        
        if (ticketTypeOpt.isPresent()) {
            return ticketTypeOpt;
        } else {
            throw new ResourceNotFoundException("TicketType not found with ID: " + id);
        }
    }

    @Override
    public List<TicketType> getAll() {
        return repository.findAll();
    }

    @Override
    public TicketType update(TicketType entity) {
        try {
            if (entity.getTicketTypeId() == null) {
                throw new ResourceNotFoundException("Cannot update. TicketType ID is missing.");
            }
            
            boolean exists = repository.existsById(entity.getTicketTypeId());
            if (exists == false) {
                throw new ResourceNotFoundException("Cannot update. TicketType not found with ID: " + entity.getTicketTypeId());
            }
            
            return repository.save(entity);
            
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (DataIntegrityViolationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while updating TicketType", ex);
        }
    }

    @Override
    public void delete(Integer id) {
        boolean exists = repository.existsById(id);
        
        if (exists == false) {
            throw new ResourceNotFoundException("Cannot delete. TicketType not found with ID: " + id);
        }
        
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
             throw new DataIntegrityViolationException("Cannot delete TicketType because it is assigned to existing tickets.", ex);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while deleting TicketType with ID: " + id, ex);
        }
    }
}
