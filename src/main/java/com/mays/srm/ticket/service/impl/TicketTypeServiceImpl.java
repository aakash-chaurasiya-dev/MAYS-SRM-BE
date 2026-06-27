package com.mays.srm.ticket.service.impl;
import com.mays.srm.ticket.entities.Ticket;
import com.mays.srm.dao.core.TicketTypeDao;
import com.mays.srm.ticket.dto.request.TicketTypeRequestDTO;
import com.mays.srm.ticket.dto.resDTO.TicketTypeResponseDTO;
import com.mays.srm.ticket.entities.TicketType;
import com.mays.srm.exception.InternalServerException;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.ticket.service.TicketTypeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TicketTypeServiceImpl implements TicketTypeService {

    private final TicketTypeDao repository;
    private final ModelMapper modelMapper;

    @Autowired
    public TicketTypeServiceImpl(TicketTypeDao repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
    }

    @Override
    public TicketTypeResponseDTO create(TicketTypeRequestDTO requestDTO) {
        try {
            TicketType ticketType = modelMapper.map(requestDTO, TicketType.class);
            TicketType savedTicketType = repository.save(ticketType);
            return modelMapper.map(savedTicketType, TicketTypeResponseDTO.class);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while creating Ticket Type", ex);
        }
    }

    @Override
    public TicketTypeResponseDTO getById(Integer id) {
        Optional<TicketType> ticketTypeOpt = repository.findById(id);
        if (ticketTypeOpt.isPresent()) {
            return modelMapper.map(ticketTypeOpt.get(), TicketTypeResponseDTO.class);
        } else {
            throw new ResourceNotFoundException("Ticket Type not found with ID: " + id);
        }
    }

    @Override
    public List<TicketTypeResponseDTO> getAll() {
        List<TicketType> ticketTypeList = repository.findAll();
        List<TicketTypeResponseDTO> dtoList = new ArrayList<>();
        for (TicketType ticketType : ticketTypeList) {
            dtoList.add(modelMapper.map(ticketType, TicketTypeResponseDTO.class));
        }
        return dtoList;
    }

    @Override
    public TicketTypeResponseDTO update(Integer id, TicketTypeRequestDTO requestDTO) {
        Optional<TicketType> existingOpt = repository.findById(id);
        if (existingOpt.isEmpty()) {
            throw new ResourceNotFoundException("Cannot update. Ticket Type not found with ID: " + id);
        }
        
        TicketType existingTicketType = existingOpt.get();
        modelMapper.map(requestDTO, existingTicketType);
        
        try {
            TicketType updatedTicketType = repository.save(existingTicketType);
            return modelMapper.map(updatedTicketType, TicketTypeResponseDTO.class);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while updating Ticket Type", ex);
        }
    }

    @Override
    public void delete(Integer id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete. Ticket Type not found with ID: " + id);
        }
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
            throw new DataIntegrityViolationException("Cannot delete Ticket Type because it is currently in use.", ex);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while deleting Ticket Type with ID: " + id, ex);
        }
    }
}
