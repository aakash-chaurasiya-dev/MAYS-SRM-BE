package com.mays.srm.service.impl;

import com.mays.srm.dao.core.DeviceTypeDao;
import com.mays.srm.dao.core.EmployeeDao;
import com.mays.srm.dao.core.EmployeeSpecDao;
import com.mays.srm.entity.DeviceType;
import com.mays.srm.entity.Employee;
import com.mays.srm.entity.EmployeeSpec;
import com.mays.srm.entity.EmployeeSpecId;
import com.mays.srm.exception.BadRequestException;
import com.mays.srm.exception.InternalServerException;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.service.EmployeeSpecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeSpecServiceImpl implements EmployeeSpecService {

    private final EmployeeSpecDao repository;
    private final EmployeeDao employeeDao;
    private final DeviceTypeDao deviceTypeDao;

    @Autowired
    public EmployeeSpecServiceImpl(EmployeeSpecDao repository, EmployeeDao employeeDao, DeviceTypeDao deviceTypeDao) {
        this.repository = repository;
        this.employeeDao = employeeDao;
        this.deviceTypeDao = deviceTypeDao;
    }

    @Override
    public EmployeeSpec create(EmployeeSpec entity) {
        try {
            if (entity.getEmployee() != null && entity.getEmployee().getEmployeeId() != null) {
                Optional<Employee> empOpt = employeeDao.findById(entity.getEmployee().getEmployeeId());
                if (empOpt.isPresent()) {
                    entity.setEmployee(empOpt.get());
                } else {
                    throw new ResourceNotFoundException("Employee not found with ID: " + entity.getEmployee().getEmployeeId());
                }
            } else {
                throw new BadRequestException("Valid Employee ID must be provided.");
            }

            if (entity.getDeviceType() != null && entity.getDeviceType().getDeviceTypeId() != null) {
                Optional<DeviceType> dtOpt = deviceTypeDao.findById(entity.getDeviceType().getDeviceTypeId());
                if (dtOpt.isPresent()) {
                    entity.setDeviceType(dtOpt.get());
                } else {
                    throw new ResourceNotFoundException("DeviceType not found with ID: " + entity.getDeviceType().getDeviceTypeId());
                }
            } else {
                throw new BadRequestException("Valid DeviceType ID must be provided.");
            }

            return repository.save(entity);
        } catch (ResourceNotFoundException | BadRequestException | DataIntegrityViolationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while creating EmployeeSpec", ex);
        }
    }

    @Override
    public Optional<EmployeeSpec> getById(EmployeeSpecId id) {
        Optional<EmployeeSpec> specOpt = repository.findById(id);
        if (specOpt.isPresent()) {
            return specOpt;
        } else {
            throw new ResourceNotFoundException("EmployeeSpec not found for given Employee and DeviceType.");
        }
    }

    @Override
    public List<EmployeeSpec> getAll() {
        return repository.findAll();
    }

    @Override
    public EmployeeSpec update(EmployeeSpec entity) {
        try {
            if (entity.getEmployee() == null || entity.getEmployee().getEmployeeId() == null ||
                entity.getDeviceType() == null || entity.getDeviceType().getDeviceTypeId() == null) {
                throw new BadRequestException("Employee ID and DeviceType ID must be provided to update.");
            }

            EmployeeSpecId id = new EmployeeSpecId(entity.getEmployee().getEmployeeId(), entity.getDeviceType().getDeviceTypeId());
            boolean exists = repository.existsById(id);
            if (!exists) {
                throw new ResourceNotFoundException("Cannot update. EmployeeSpec not found.");
            }

            // Since it's a join table with no extra columns, 'update' essentially does nothing new,
            // but we'll leave it implemented for standard CRUD support.
            return repository.save(entity);
        } catch (ResourceNotFoundException | BadRequestException | DataIntegrityViolationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while updating EmployeeSpec", ex);
        }
    }

    @Override
    public void delete(EmployeeSpecId id) {
        boolean exists = repository.existsById(id);
        if (!exists) {
            throw new ResourceNotFoundException("Cannot delete. EmployeeSpec not found.");
        }
        
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
            throw new DataIntegrityViolationException("Cannot delete EmployeeSpec due to database constraint.", ex);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while deleting EmployeeSpec.", ex);
        }
    }
}
