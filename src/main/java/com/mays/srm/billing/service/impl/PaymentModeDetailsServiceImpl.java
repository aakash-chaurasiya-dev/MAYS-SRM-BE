package com.mays.srm.billing.service.impl;
import com.mays.srm.billing.entities.Billing;
import com.mays.srm.billing.dto.request.PaymentModeDetailsRequestDTO;
import com.mays.srm.billing.dto.resDTO.PaymentModeDetailsResponseDTO;
import com.mays.srm.billing.entities.PaymentModeDetails;
import com.mays.srm.billing.repository.PaymentModeDetailsDao;
import com.mays.srm.exception.InternalServerException;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.billing.service.PaymentModeDetailsService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentModeDetailsServiceImpl implements PaymentModeDetailsService {

    private final PaymentModeDetailsDao repository;
    private final ModelMapper modelMapper;

    @Autowired
    public PaymentModeDetailsServiceImpl(PaymentModeDetailsDao repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
    }

    @Override
    public PaymentModeDetailsResponseDTO create(PaymentModeDetailsRequestDTO requestDTO) {
        try {
            PaymentModeDetails paymentModeDetails = modelMapper.map(requestDTO, PaymentModeDetails.class);
            PaymentModeDetails savedDetails = repository.save(paymentModeDetails);
            return modelMapper.map(savedDetails, PaymentModeDetailsResponseDTO.class);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while creating Payment Mode", ex);
        }
    }

    @Override
    public PaymentModeDetailsResponseDTO getById(Integer id) {
        Optional<PaymentModeDetails> detailsOpt = repository.findById(id);
        if (detailsOpt.isPresent()) {
            return modelMapper.map(detailsOpt.get(), PaymentModeDetailsResponseDTO.class);
        } else {
            throw new ResourceNotFoundException("Payment Mode not found with ID: " + id);
        }
    }

    @Override
    public List<PaymentModeDetailsResponseDTO> getAll() {
        List<PaymentModeDetails> detailsList = repository.findAll();
        List<PaymentModeDetailsResponseDTO> dtoList = new ArrayList<>();
        for (PaymentModeDetails details : detailsList) {
            dtoList.add(modelMapper.map(details, PaymentModeDetailsResponseDTO.class));
        }
        return dtoList;
    }

    @Override
    public PaymentModeDetailsResponseDTO update(Integer id, PaymentModeDetailsRequestDTO requestDTO) {
        Optional<PaymentModeDetails> existingOpt = repository.findById(id);
        if (existingOpt.isEmpty()) {
            throw new ResourceNotFoundException("Cannot update. Payment Mode not found with ID: " + id);
        }
        
        PaymentModeDetails existingDetails = existingOpt.get();
        modelMapper.map(requestDTO, existingDetails);
        existingDetails.setPayModeId(id);
        try {
            PaymentModeDetails updatedDetails = repository.save(existingDetails);
            return modelMapper.map(updatedDetails, PaymentModeDetailsResponseDTO.class);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while updating Payment Mode", ex);
        }
    }

    @Override
    public void delete(Integer id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete. Payment Mode not found with ID: " + id);
        }
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
            throw new DataIntegrityViolationException("Cannot delete Payment Mode because it is currently in use by a Billing record.", ex);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while deleting Payment Mode with ID: " + id, ex);
        }
    }
}
