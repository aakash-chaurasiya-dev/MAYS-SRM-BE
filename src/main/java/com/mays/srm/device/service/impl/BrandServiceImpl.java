package com.mays.srm.device.service.impl;
import com.mays.srm.device.entities.Device;
import com.mays.srm.device.dto.request.BrandRequestDTO;
import com.mays.srm.device.dto.resDTO.BrandResponseDTO;
import com.mays.srm.device.entities.Brand;
import com.mays.srm.device.entities.DeviceType;
import com.mays.srm.device.repository.BrandDao;
import com.mays.srm.device.repository.DeviceTypeDao;
import com.mays.srm.exception.InternalServerException;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.device.service.BrandService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BrandServiceImpl implements BrandService {

    private final BrandDao repository;
    private final DeviceTypeDao deviceTypeDao;
    private final ModelMapper modelMapper;

    @Autowired
    public BrandServiceImpl(BrandDao repository, DeviceTypeDao deviceTypeDao, ModelMapper modelMapper) {
        this.repository = repository;
        this.deviceTypeDao = deviceTypeDao;
        this.modelMapper = modelMapper;
    }

    @Override
    public BrandResponseDTO create(BrandRequestDTO requestDTO) {
        try {
            Brand brand = modelMapper.map(requestDTO, Brand.class);
            
            if (requestDTO.getDeviceTypeId() != null) {
                Optional<DeviceType> deviceTypeOpt = deviceTypeDao.findById(requestDTO.getDeviceTypeId());
                if (deviceTypeOpt.isPresent()) {
                    brand.setDeviceType(deviceTypeOpt.get());
                } else {
                    throw new ResourceNotFoundException("Device Type not found with ID: " + requestDTO.getDeviceTypeId());
                }
            }

            Brand savedBrand = repository.save(brand);
            return mapToResponseDTO(savedBrand);
        } catch (ResourceNotFoundException | DataIntegrityViolationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while creating Brand", ex);
        }
    }

    @Override
    public BrandResponseDTO getById(Integer id) {
        Optional<Brand> brandOpt = repository.findById(id);
        if (brandOpt.isPresent()) {
            return mapToResponseDTO(brandOpt.get());
        } else {
            throw new ResourceNotFoundException("Brand not found with ID: " + id);
        }
    }

    @Override
    public List<BrandResponseDTO> getAll() {
        List<Brand> brandList = repository.findAll();
        List<BrandResponseDTO> dtoList = new ArrayList<>();
        for (Brand brand : brandList) {
            dtoList.add(mapToResponseDTO(brand));
        }
        return dtoList;
    }

    @Override
    public BrandResponseDTO update(Integer id, BrandRequestDTO requestDTO) {
        Optional<Brand> existingOpt = repository.findById(id);
        if (existingOpt.isEmpty()) {
            throw new ResourceNotFoundException("Cannot update. Brand not found with ID: " + id);
        }
        
        Brand existingBrand = existingOpt.get();
        modelMapper.map(requestDTO, existingBrand);

        if (requestDTO.getDeviceTypeId() != null) {
            Optional<DeviceType> deviceTypeOpt = deviceTypeDao.findById(requestDTO.getDeviceTypeId());
            if (deviceTypeOpt.isPresent()) {
                existingBrand.setDeviceType(deviceTypeOpt.get());
            } else {
                throw new ResourceNotFoundException("Device Type not found with ID: " + requestDTO.getDeviceTypeId());
            }
        } else {
            existingBrand.setDeviceType(null);
        }
        
        try {
            Brand updatedBrand = repository.save(existingBrand);
            return mapToResponseDTO(updatedBrand);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while updating Brand", ex);
        }
    }

    @Override
    public void delete(Integer id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete. Brand not found with ID: " + id);
        }
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
            throw new DataIntegrityViolationException("Cannot delete Brand because it is currently in use.", ex);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while deleting Brand with ID: " + id, ex);
        }
    }

    private BrandResponseDTO mapToResponseDTO(Brand brand) {
        BrandResponseDTO dto = modelMapper.map(brand, BrandResponseDTO.class);
        if (brand.getDeviceType() != null) {
            dto.setDeviceTypeName(brand.getDeviceType().getDeviceTypeName());
        }
        return dto;
    }
}
