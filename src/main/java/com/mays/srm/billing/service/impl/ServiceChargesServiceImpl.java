package com.mays.srm.billing.service.impl;
import com.mays.srm.billing.entities.Billing;
import com.mays.srm.dao.core.BrandDao;
import com.mays.srm.dao.core.ServiceChargesDao;
import com.mays.srm.billing.dto.request.ServiceChargesRequestDTO;
import com.mays.srm.billing.dto.resDTO.ServiceChargesResponseDTO;
import com.mays.srm.device.entities.Brand;
import com.mays.srm.billing.entities.ServiceCharges;
import com.mays.srm.exception.InternalServerException;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.billing.service.ServiceChargesService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ServiceChargesServiceImpl implements ServiceChargesService {

    private final ServiceChargesDao repository;
    private final BrandDao brandDao;
    private final ModelMapper modelMapper;

    @Autowired
    public ServiceChargesServiceImpl(ServiceChargesDao repository, BrandDao brandDao, ModelMapper modelMapper) {
        this.repository = repository;
        this.brandDao = brandDao;
        this.modelMapper = modelMapper;
    }

    @Override
    public ServiceChargesResponseDTO create(ServiceChargesRequestDTO requestDTO) {
        try {
            ServiceCharges serviceCharge = modelMapper.map(requestDTO, ServiceCharges.class);
            validateAndSetRelations(serviceCharge, requestDTO);
            
            ServiceCharges savedCharge = repository.save(serviceCharge);
            return mapToResponseDTO(savedCharge);
        } catch (ResourceNotFoundException | DataIntegrityViolationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while creating Service Charge", ex);
        }
    }

    @Override
    public ServiceChargesResponseDTO getById(Integer id) {
        Optional<ServiceCharges> chargeOpt = repository.findById(id);
        if (chargeOpt.isPresent()) {
            return mapToResponseDTO(chargeOpt.get());
        } else {
            throw new ResourceNotFoundException("Service Charge not found with ID: " + id);
        }
    }

    @Override
    public List<ServiceChargesResponseDTO> getAll() {
        List<ServiceCharges> chargeList = repository.findAll();
        List<ServiceChargesResponseDTO> dtoList = new ArrayList<>();
        for (ServiceCharges charge : chargeList) {
            dtoList.add(mapToResponseDTO(charge));
        }
        return dtoList;
    }

    @Override
    public ServiceChargesResponseDTO update(Integer id, ServiceChargesRequestDTO requestDTO) {
        Optional<ServiceCharges> existingOpt = repository.findById(id);
        if (existingOpt.isEmpty()) {
            throw new ResourceNotFoundException("Cannot update. Service Charge not found with ID: " + id);
        }
        
        ServiceCharges existingCharge = existingOpt.get();
        modelMapper.map(requestDTO, existingCharge);
        existingCharge.setChargeId(id);

        try {
            validateAndSetRelations(existingCharge, requestDTO);
            ServiceCharges updatedCharge = repository.save(existingCharge);
            return mapToResponseDTO(updatedCharge);
        } catch (ResourceNotFoundException | DataIntegrityViolationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while updating Service Charge", ex);
        }
    }

    @Override
    public void delete(Integer id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete. Service Charge not found with ID: " + id);
        }
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
            throw new DataIntegrityViolationException("Cannot delete Service Charge because it is assigned to existing Billing records.", ex);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while deleting Service Charge with ID: " + id, ex);
        }
    }

    private void validateAndSetRelations(ServiceCharges entity, ServiceChargesRequestDTO requestDTO) {
        if (requestDTO.getBrandId() != null) {
            Optional<Brand> brandOpt = brandDao.findById(requestDTO.getBrandId());
            if (brandOpt.isPresent()) {
                entity.setBrand(brandOpt.get());
            } else {
                throw new ResourceNotFoundException("Brand not found with ID: " + requestDTO.getBrandId());
            }
        } else {
            entity.setBrand(null);
        }
    }

    private ServiceChargesResponseDTO mapToResponseDTO(ServiceCharges charge) {
        ServiceChargesResponseDTO dto = modelMapper.map(charge, ServiceChargesResponseDTO.class);
        if (charge.getBrand() != null) {
            dto.setBrandName(charge.getBrand().getBrandName());
        }
        return dto;
    }
}
