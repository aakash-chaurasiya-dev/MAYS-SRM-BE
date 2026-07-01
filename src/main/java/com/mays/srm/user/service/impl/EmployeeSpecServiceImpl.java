package com.mays.srm.user.service.impl;
import com.mays.srm.dao.core.DeviceTypeDao;
import com.mays.srm.dao.core.EmployeeDao;
import com.mays.srm.dao.core.EmployeeSpecDao;
import com.mays.srm.user.dto.request.EmployeeSpecRequestDTO;
import com.mays.srm.user.dto.resDTO.EmployeeSpecResponseDTO;
import com.mays.srm.device.entities.DeviceType;
import com.mays.srm.user.entities.Employee;
import com.mays.srm.user.entities.EmployeeSpec;
import com.mays.srm.entity.EmployeeSpecId;
import com.mays.srm.exception.InternalServerException;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.user.service.EmployeeSpecService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeSpecServiceImpl implements EmployeeSpecService {

    private final EmployeeSpecDao repository;
    private final EmployeeDao employeeDao;
    private final DeviceTypeDao deviceTypeDao;
    private final ModelMapper modelMapper;

    @Autowired
    public EmployeeSpecServiceImpl(EmployeeSpecDao repository, EmployeeDao employeeDao, DeviceTypeDao deviceTypeDao, ModelMapper modelMapper) {
        this.repository = repository;
        this.employeeDao = employeeDao;
        this.deviceTypeDao = deviceTypeDao;
        this.modelMapper = modelMapper;
    }

    @Override
    public EmployeeSpecResponseDTO create(EmployeeSpecRequestDTO requestDTO) {
        try {
            EmployeeSpec employeeSpec = new EmployeeSpec();
            
            Optional<Employee> empOpt = employeeDao.findById(requestDTO.getEmployeeId());
            if (empOpt.isEmpty()) {
                throw new ResourceNotFoundException("Employee not found with ID: " + requestDTO.getEmployeeId());
            }
            employeeSpec.setEmployee(empOpt.get());

            Optional<DeviceType> dtOpt = deviceTypeDao.findById(requestDTO.getDeviceTypeId());
            if (dtOpt.isEmpty()) {
                throw new ResourceNotFoundException("DeviceType not found with ID: " + requestDTO.getDeviceTypeId());
            }
            employeeSpec.setDeviceType(dtOpt.get());

            EmployeeSpec savedSpec = repository.save(employeeSpec);
            return mapToResponseDTO(savedSpec);
        } catch (ResourceNotFoundException | DataIntegrityViolationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while creating Employee Specialization", ex);
        }
    }

    @Override
    public List<EmployeeSpecResponseDTO> getAll() {
        List<EmployeeSpec> specList = repository.findAll();
        List<EmployeeSpecResponseDTO> dtoList = new ArrayList<>();
        for (EmployeeSpec spec : specList) {
            dtoList.add(mapToResponseDTO(spec));
        }
        return dtoList;
    }

    @Override
    public void delete(Integer employeeId, Integer deviceTypeId) {
        EmployeeSpecId id = new EmployeeSpecId(employeeId, deviceTypeId);
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Employee Specialization not found.");
        }
        try {
            repository.deleteById(id);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while deleting Employee Specialization", ex);
        }
    }

    private EmployeeSpecResponseDTO mapToResponseDTO(EmployeeSpec spec) {
        EmployeeSpecResponseDTO dto = new EmployeeSpecResponseDTO();
        if (spec.getEmployee() != null) {
            dto.setEmployeeId(spec.getEmployee().getEmployeeId());
            dto.setEmployeeName(spec.getEmployee().getEmployeeName());
        }
        if (spec.getDeviceType() != null) {
            dto.setDeviceTypeId(spec.getDeviceType().getDeviceTypeId());
            dto.setDeviceTypeName(spec.getDeviceType().getDeviceTypeName());
        }
        return dto;
    }
}
