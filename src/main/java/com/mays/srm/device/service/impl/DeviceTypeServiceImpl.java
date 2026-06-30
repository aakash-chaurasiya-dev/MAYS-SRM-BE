package com.mays.srm.device.service.impl;
import com.mays.srm.device.entities.Device;
import com.mays.srm.device.dto.request.DeviceTypeRequestDTO;
import com.mays.srm.device.dto.resDTO.DeviceTypeResponseDTO;
import com.mays.srm.device.entities.DeviceType;
import com.mays.srm.device.repository.DeviceTypeDao;
import com.mays.srm.exception.InternalServerException;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.device.service.DeviceTypeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DeviceTypeServiceImpl implements DeviceTypeService {

    private final DeviceTypeDao repository;
    private final ModelMapper modelMapper;

    @Autowired
    public DeviceTypeServiceImpl(DeviceTypeDao repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
    }

    @Override
    public DeviceTypeResponseDTO create(DeviceTypeRequestDTO requestDTO) {
        try {
            DeviceType deviceType = modelMapper.map(requestDTO, DeviceType.class);
            DeviceType savedDeviceType = repository.save(deviceType);
            return modelMapper.map(savedDeviceType, DeviceTypeResponseDTO.class);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while creating Device Type", ex);
        }
    }

    @Override
    public DeviceTypeResponseDTO getById(Integer id) {
        Optional<DeviceType> deviceTypeOpt = repository.findById(id);
        if (deviceTypeOpt.isPresent()) {
            return modelMapper.map(deviceTypeOpt.get(), DeviceTypeResponseDTO.class);
        } else {
            throw new ResourceNotFoundException("Device Type not found with ID: " + id);
        }
    }

    @Override
    public List<DeviceTypeResponseDTO> getAll() {
        List<DeviceType> deviceTypeList = repository.findAll();
        List<DeviceTypeResponseDTO> dtoList = new ArrayList<>();
        for (DeviceType deviceType : deviceTypeList) {
            dtoList.add(modelMapper.map(deviceType, DeviceTypeResponseDTO.class));
        }
        return dtoList;
    }

    @Override
    public DeviceTypeResponseDTO update(Integer id, DeviceTypeRequestDTO requestDTO) {
        Optional<DeviceType> existingOpt = repository.findById(id);
        if (existingOpt.isEmpty()) {
            throw new ResourceNotFoundException("Cannot update. Device Type not found with ID: " + id);
        }
        
        DeviceType existingDeviceType = existingOpt.get();
        modelMapper.map(requestDTO, existingDeviceType);
        
        try {
            DeviceType updatedDeviceType = repository.save(existingDeviceType);
            return modelMapper.map(updatedDeviceType, DeviceTypeResponseDTO.class);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while updating Device Type", ex);
        }
    }

    @Override
    public void delete(Integer id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete. Device Type not found with ID: " + id);
        }
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
            throw new DataIntegrityViolationException("Cannot delete Device Type because it is currently in use.", ex);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while deleting Device Type with ID: " + id, ex);
        }
    }
}
