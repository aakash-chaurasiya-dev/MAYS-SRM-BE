package com.mays.srm.service.impl;

import com.mays.srm.dao.core.BrandDao;
import com.mays.srm.dao.core.DeviceModelDao;
import com.mays.srm.dto.requestDTO.DeviceModelRequestDTO;
import com.mays.srm.dto.responseDTO.DeviceModelResponseDTO;
import com.mays.srm.entity.Brand;
import com.mays.srm.entity.DeviceModel;
import com.mays.srm.exception.InternalServerException;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.service.DeviceModelService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DeviceModelServiceImpl implements DeviceModelService {

    private final DeviceModelDao repository;
    private final BrandDao brandDao;
    private final ModelMapper modelMapper;

    @Autowired
    public DeviceModelServiceImpl(DeviceModelDao repository, BrandDao brandDao, ModelMapper modelMapper) {
        this.repository = repository;
        this.brandDao = brandDao;
        this.modelMapper = modelMapper;
    }

    @Override
    public DeviceModelResponseDTO create(DeviceModelRequestDTO requestDTO) {
        try {
            DeviceModel deviceModel = modelMapper.map(requestDTO, DeviceModel.class);
            
            if (requestDTO.getBrandId() != null) {
                Optional<Brand> brandOpt = brandDao.findById(requestDTO.getBrandId());
                if (brandOpt.isPresent()) {
                    deviceModel.setBrand(brandOpt.get());
                } else {
                    throw new ResourceNotFoundException("Brand not found with ID: " + requestDTO.getBrandId());
                }
            }

            DeviceModel savedDeviceModel = repository.save(deviceModel);
            return mapToResponseDTO(savedDeviceModel);
        } catch (ResourceNotFoundException | DataIntegrityViolationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while creating Device Model", ex);
        }
    }

    @Override
    public DeviceModelResponseDTO getById(Integer id) {
        Optional<DeviceModel> deviceModelOpt = repository.findById(id);
        if (deviceModelOpt.isPresent()) {
            return mapToResponseDTO(deviceModelOpt.get());
        } else {
            throw new ResourceNotFoundException("Device Model not found with ID: " + id);
        }
    }

    @Override
    public List<DeviceModelResponseDTO> getAll() {
        List<DeviceModel> deviceModelList = repository.findAll();
        List<DeviceModelResponseDTO> dtoList = new ArrayList<>();
        for (DeviceModel deviceModel : deviceModelList) {
            dtoList.add(mapToResponseDTO(deviceModel));
        }
        return dtoList;
    }

    @Override
    public DeviceModelResponseDTO update(Integer id, DeviceModelRequestDTO requestDTO) {
        Optional<DeviceModel> existingOpt = repository.findById(id);
        if (existingOpt.isEmpty()) {
            throw new ResourceNotFoundException("Cannot update. Device Model not found with ID: " + id);
        }
        
        DeviceModel existingDeviceModel = existingOpt.get();
        modelMapper.map(requestDTO, existingDeviceModel);

        if (requestDTO.getBrandId() != null) {
            Optional<Brand> brandOpt = brandDao.findById(requestDTO.getBrandId());
            if (brandOpt.isPresent()) {
                existingDeviceModel.setBrand(brandOpt.get());
            } else {
                throw new ResourceNotFoundException("Brand not found with ID: " + requestDTO.getBrandId());
            }
        } else {
            existingDeviceModel.setBrand(null);
        }
        
        try {
            DeviceModel updatedDeviceModel = repository.save(existingDeviceModel);
            return mapToResponseDTO(updatedDeviceModel);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while updating Device Model", ex);
        }
    }

    @Override
    public void delete(Integer id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete. Device Model not found with ID: " + id);
        }
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
            throw new DataIntegrityViolationException("Cannot delete Device Model because it is currently in use.", ex);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while deleting Device Model with ID: " + id, ex);
        }
    }

    private DeviceModelResponseDTO mapToResponseDTO(DeviceModel deviceModel) {
        DeviceModelResponseDTO dto = modelMapper.map(deviceModel, DeviceModelResponseDTO.class);
        if (deviceModel.getBrand() != null) {
            dto.setBrandName(deviceModel.getBrand().getBrandName());
        }
        return dto;
    }
}
