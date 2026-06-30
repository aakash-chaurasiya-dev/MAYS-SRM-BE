package com.mays.srm.device.service.impl;
import com.mays.srm.device.dto.request.DeviceRequestDTO;
import com.mays.srm.device.dto.resDTO.DeviceResponseDTO;
import com.mays.srm.device.entities.Device;
import com.mays.srm.device.entities.DeviceModel;
import com.mays.srm.device.repository.DeviceDao;
import com.mays.srm.device.repository.DeviceModelDao;
import com.mays.srm.exception.InternalServerException;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.device.service.DeviceService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DeviceServiceImpl implements DeviceService {

    private final DeviceDao repository;
    private final DeviceModelDao deviceModelDao;
    private final ModelMapper modelMapper;

    @Autowired
    public DeviceServiceImpl(DeviceDao repository, DeviceModelDao deviceModelDao, ModelMapper modelMapper) {
        this.repository = repository;
        this.deviceModelDao = deviceModelDao;
        this.modelMapper = modelMapper;
    }

    @Override
    public DeviceResponseDTO create(DeviceRequestDTO requestDTO) {
        try {
            Device device = modelMapper.map(requestDTO, Device.class);
            
            if (requestDTO.getModelId() != null) {
                Optional<DeviceModel> modelOpt = deviceModelDao.findById(requestDTO.getModelId());
                if (modelOpt.isPresent()) {
                    device.setModel(modelOpt.get());
                } else {
                    throw new ResourceNotFoundException("Device Model not found with ID: " + requestDTO.getModelId());
                }
            }

            Device savedDevice = repository.save(device);
            return mapToResponseDTO(savedDevice);
        } catch (ResourceNotFoundException | DataIntegrityViolationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while creating Device", ex);
        }
    }

    @Override
    public DeviceResponseDTO getById(String id) {
        Optional<Device> deviceOpt = repository.findById(id);
        if (deviceOpt.isPresent()) {
            return mapToResponseDTO(deviceOpt.get());
        } else {
            throw new ResourceNotFoundException("Device not found with Serial No: " + id);
        }
    }

    @Override
    public List<DeviceResponseDTO> getAll() {
        List<Device> deviceList = repository.findAll();
        List<DeviceResponseDTO> dtoList = new ArrayList<>();
        for (Device device : deviceList) {
            dtoList.add(mapToResponseDTO(device));
        }
        return dtoList;
    }

    @Override
    public DeviceResponseDTO update(String id, DeviceRequestDTO requestDTO) {
        Optional<Device> existingOpt = repository.findById(id);
        if (existingOpt.isEmpty()) {
            throw new ResourceNotFoundException("Cannot update. Device not found with Serial No: " + id);
        }
        
        Device existingDevice = existingOpt.get();
        modelMapper.map(requestDTO, existingDevice);
        
        // Ensure ID is not changed during update
        existingDevice.setSerialNo(id);

        if (requestDTO.getModelId() != null) {
            Optional<DeviceModel> modelOpt = deviceModelDao.findById(requestDTO.getModelId());
            if (modelOpt.isPresent()) {
                existingDevice.setModel(modelOpt.get());
            } else {
                throw new ResourceNotFoundException("Device Model not found with ID: " + requestDTO.getModelId());
            }
        } else {
            existingDevice.setModel(null);
        }
        
        try {
            Device updatedDevice = repository.save(existingDevice);
            return mapToResponseDTO(updatedDevice);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while updating Device", ex);
        }
    }

    @Override
    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete. Device not found with Serial No: " + id);
        }
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
            throw new DataIntegrityViolationException("Cannot delete Device because it is currently linked to tickets.", ex);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while deleting Device with Serial No: " + id, ex);
        }
    }

    private DeviceResponseDTO mapToResponseDTO(Device device) {
        DeviceResponseDTO dto = modelMapper.map(device, DeviceResponseDTO.class);
        if (device.getModel() != null) {
            dto.setModelName(device.getModel().getModelName());
            if (device.getModel().getBrand() != null) {
                dto.setBrandName(device.getModel().getBrand().getBrandName());
            }
        }
        return dto;
    }
}
