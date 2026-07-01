package com.mays.srm.billing.service.impl;
import com.mays.srm.billing.entities.Billing;
import com.mays.srm.dao.core.ChargeTypeDao;
import com.mays.srm.billing.dto.request.ChargeTypeRequestDTO;
import com.mays.srm.billing.dto.resDTO.ChargeTypeResponseDTO;
import com.mays.srm.billing.dto.resDTO.ServiceChargesResponseDTO;
import com.mays.srm.billing.entities.ChargeType;
import com.mays.srm.billing.entities.ServiceCharges;
import com.mays.srm.exception.InternalServerException;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.billing.service.ChargeTypeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ChargeTypeServiceImpl implements ChargeTypeService {

    private final ChargeTypeDao repository;
    private final ModelMapper modelMapper;

    @Autowired
    public ChargeTypeServiceImpl(ChargeTypeDao repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ChargeTypeResponseDTO create(ChargeTypeRequestDTO requestDTO) {
        try {
            ChargeType chargeType = modelMapper.map(requestDTO, ChargeType.class);
            ChargeType savedChargeType = repository.save(chargeType);
            return mapToResponseDTO(savedChargeType);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while creating Charge Type", ex);
        }
    }

    @Override
    public ChargeTypeResponseDTO getById(Integer id) {
        Optional<ChargeType> chargeTypeOpt = repository.findById(id);
        if (chargeTypeOpt.isPresent()) {
            return mapToResponseDTO(chargeTypeOpt.get());
        } else {
            throw new ResourceNotFoundException("Charge Type not found with ID: " + id);
        }
    }

    @Override
    public List<ChargeTypeResponseDTO> getAll() {
        List<ChargeType> chargeTypeList = repository.findAll();
        List<ChargeTypeResponseDTO> dtoList = new ArrayList<>();
        for (ChargeType chargeType : chargeTypeList) {
            dtoList.add(mapToResponseDTO(chargeType));
        }
        return dtoList;
    }

    @Override
    public ChargeTypeResponseDTO update(Integer id, ChargeTypeRequestDTO requestDTO) {
        Optional<ChargeType> existingOpt = repository.findById(id);
        if (existingOpt.isEmpty()) {
            throw new ResourceNotFoundException("Cannot update. Charge Type not found with ID: " + id);
        }
        
        ChargeType existingChargeType = existingOpt.get();
        modelMapper.map(requestDTO, existingChargeType);
        existingChargeType.setChargeTypeId(id);
        try {
            ChargeType updatedChargeType = repository.save(existingChargeType);
            return mapToResponseDTO(updatedChargeType);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while updating Charge Type", ex);
        }
    }

    @Override
    public void delete(Integer id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete. Charge Type not found with ID: " + id);
        }
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
            throw new DataIntegrityViolationException("Cannot delete Charge Type because it is currently in use by a Billing record.", ex);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while deleting Charge Type with ID: " + id, ex);
        }
    }

    private ChargeTypeResponseDTO mapToResponseDTO(ChargeType chargeType) {
        ChargeTypeResponseDTO dto = modelMapper.map(chargeType, ChargeTypeResponseDTO.class);
        return dto;
    }
}
