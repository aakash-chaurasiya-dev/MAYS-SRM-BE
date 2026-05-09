package com.mays.srm.service.impl;

import com.mays.srm.dao.core.DeviceTypeDao;
import com.mays.srm.dao.core.PartsDao;
import com.mays.srm.dao.core.StatusDao;
import com.mays.srm.dao.core.TicketDao;
import com.mays.srm.entity.DeviceType;
import com.mays.srm.entity.Parts;
import com.mays.srm.entity.Status;
import com.mays.srm.entity.Ticket;
import com.mays.srm.exception.BadRequestException;
import com.mays.srm.exception.InternalServerException;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.service.PartsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PartsServiceImpl implements PartsService {

    private final PartsDao repository;
    private final StatusDao statusDao;
    private final TicketDao ticketDao;
    private final DeviceTypeDao deviceTypeDao;

    @Autowired
    public PartsServiceImpl(PartsDao repository, StatusDao statusDao, TicketDao ticketDao, DeviceTypeDao deviceTypeDao) {
        this.repository = repository;
        this.statusDao = statusDao;
        this.ticketDao = ticketDao;
        this.deviceTypeDao = deviceTypeDao;
    }

    @Override
    public Parts create(Parts entity) {
        try {
            validateAndSetRelations(entity);
            return repository.save(entity);
        } catch (ResourceNotFoundException | BadRequestException | DataIntegrityViolationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while creating Part", ex);
        }
    }

    @Override
    public Optional<Parts> getById(Integer id) {
        Optional<Parts> partOpt = repository.findById(id);
        if (partOpt.isPresent()) {
            return partOpt;
        } else {
            throw new ResourceNotFoundException("Part not found with ID: " + id);
        }
    }

    @Override
    public List<Parts> getAll() {
        return repository.findAll();
    }

    @Override
    public Parts update(Parts entity) {
        try {
            if (entity.getPartId() == null) {
                throw new ResourceNotFoundException("Cannot update. Part ID is missing.");
            }
            
            boolean exists = repository.existsById(entity.getPartId());
            if (!exists) {
                throw new ResourceNotFoundException("Cannot update. Part not found with ID: " + entity.getPartId());
            }

            validateAndSetRelations(entity);

            return repository.save(entity);
        } catch (ResourceNotFoundException | BadRequestException | DataIntegrityViolationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while updating Part", ex);
        }
    }

    @Override
    public void delete(Integer id) {
        boolean exists = repository.existsById(id);
        if (!exists) {
            throw new ResourceNotFoundException("Cannot delete. Part not found with ID: " + id);
        }
        
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
            throw new DataIntegrityViolationException("Cannot delete Part due to database constraints.", ex);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while deleting Part with ID: " + id, ex);
        }
    }

    /**
     * Helper method to validate and set Status, Ticket, and DeviceType relations
     */
    private void validateAndSetRelations(Parts entity) {
        // Validate Ticket
        if (entity.getTicket() != null && entity.getTicket().getTicketId() != null) {
            Optional<Ticket> ticketOpt = ticketDao.findById(entity.getTicket().getTicketId());
            if (ticketOpt.isPresent()) {
                entity.setTicket(ticketOpt.get());
            } else {
                throw new ResourceNotFoundException("Ticket not found with ID: " + entity.getTicket().getTicketId());
            }
        }

        // Validate DeviceType
        if (entity.getDeviceType() != null && entity.getDeviceType().getDeviceTypeId() != null) {
            Optional<DeviceType> dtOpt = deviceTypeDao.findById(entity.getDeviceType().getDeviceTypeId());
            if (dtOpt.isPresent()) {
                entity.setDeviceType(dtOpt.get());
            } else {
                throw new ResourceNotFoundException("DeviceType not found with ID: " + entity.getDeviceType().getDeviceTypeId());
            }
        }

        // Validate Status
        if (entity.getStatus() != null && entity.getStatus().getStatusId() != null) {
            Optional<Status> statusOpt = statusDao.findById(entity.getStatus().getStatusId());
            if (statusOpt.isPresent()) {
                Status dbStatus = statusOpt.get();
                // Ensure the status is specifically meant for "parts"
                if (!"parts".equalsIgnoreCase(dbStatus.getStatusType())) {
                    throw new BadRequestException("Status must be of type 'parts'");
                }
                entity.setStatus(dbStatus);
            } else {
                throw new ResourceNotFoundException("Status not found with ID: " + entity.getStatus().getStatusId());
            }
        }
    }
}
