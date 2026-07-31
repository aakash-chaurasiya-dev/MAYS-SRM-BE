package com.mays.srm.ticket.service.impl;

import com.mays.srm.ticket.repository.WarrantyTypeDao;
import com.mays.srm.ticket.repository.TicketTypeDao;
import com.mays.srm.ticket.dto.request.WarrantyTypeRequestDTO;
import com.mays.srm.ticket.dto.resDTO.WarrantyTypeResponseDTO;
import com.mays.srm.ticket.entities.WarrantyType;
import com.mays.srm.ticket.entities.TicketType;
import com.mays.srm.exception.InternalServerException;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.ticket.service.WarrantyTypeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class WarrantyTypeServiceImpl implements WarrantyTypeService {

    private final WarrantyTypeDao repository;
    private final TicketTypeDao ticketTypeDao;
    private final ModelMapper modelMapper;

    @Autowired
    public WarrantyTypeServiceImpl(WarrantyTypeDao repository, TicketTypeDao ticketTypeDao, ModelMapper modelMapper) {
        this.repository = repository;
        this.ticketTypeDao = ticketTypeDao;
        this.modelMapper = modelMapper;
    }

    @Override
    @CacheEvict(value = "warrantyTypes", allEntries = true)
    public WarrantyTypeResponseDTO create(WarrantyTypeRequestDTO requestDTO) {
        try {
            WarrantyType warrantyType = modelMapper.map(requestDTO, WarrantyType.class);
            if (requestDTO.getTicketTypeId() != null) {
                TicketType tt = ticketTypeDao.findById(requestDTO.getTicketTypeId())
                        .orElseThrow(() -> new ResourceNotFoundException("Ticket Type not found with ID: " + requestDTO.getTicketTypeId()));
                warrantyType.setTicketType(tt);
            }
            WarrantyType saved = repository.save(warrantyType);
            return mapToResponse(saved);
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while creating Warranty Type", ex);
        }
    }

    @Override
    @Cacheable(value = "warrantyTypes", key = "#id")
    public WarrantyTypeResponseDTO getById(Integer id) {
        Optional<WarrantyType> opt = repository.findById(id);
        if (opt.isPresent()) {
            return mapToResponse(opt.get());
        } else {
            throw new ResourceNotFoundException("Warranty Type not found with ID: " + id);
        }
    }

    @Override
    @Cacheable(value = "warrantyTypes", key = "'all'")
    public List<WarrantyTypeResponseDTO> getAll() {
        List<WarrantyType> list = repository.findAll();
        List<WarrantyTypeResponseDTO> dtoList = new ArrayList<>();
        for (WarrantyType wt : list) {
            dtoList.add(mapToResponse(wt));
        }
        return dtoList;
    }

    @Override
    @CacheEvict(value = "warrantyTypes", allEntries = true)
    public WarrantyTypeResponseDTO update(Integer id, WarrantyTypeRequestDTO requestDTO) {
        Optional<WarrantyType> existingOpt = repository.findById(id);
        if (existingOpt.isEmpty()) {
            throw new ResourceNotFoundException("Cannot update. Warranty Type not found with ID: " + id);
        }
        
        WarrantyType existing = existingOpt.get();
        existing.setWarrantyTypeName(requestDTO.getWarrantyTypeName());
        existing.setWarrantyTypeDescription(requestDTO.getWarrantyTypeDescription());
        if (requestDTO.getIsLocked() != null) {
            existing.setIsLocked(requestDTO.getIsLocked());
        }
        
        if (requestDTO.getTicketTypeId() != null) {
            TicketType tt = ticketTypeDao.findById(requestDTO.getTicketTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Ticket Type not found with ID: " + requestDTO.getTicketTypeId()));
            existing.setTicketType(tt);
        } else {
            existing.setTicketType(null);
        }
        
        try {
            WarrantyType updated = repository.save(existing);
            return mapToResponse(updated);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while updating Warranty Type", ex);
        }
    }

    @Override
    @CacheEvict(value = "warrantyTypes", allEntries = true)
    public void delete(Integer id) {
        Optional<WarrantyType> existingOpt = repository.findById(id);
        if (existingOpt.isEmpty()) {
            throw new ResourceNotFoundException("Cannot delete. Warranty Type not found with ID: " + id);
        }
        if (Boolean.TRUE.equals(existingOpt.get().getIsLocked())) {
            throw new IllegalArgumentException("Warranty Type is locked and cannot be deleted.");
        }
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
            throw new DataIntegrityViolationException("Cannot delete Warranty Type because it is currently in use.", ex);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while deleting Warranty Type with ID: " + id, ex);
        }
    }

    private WarrantyTypeResponseDTO mapToResponse(WarrantyType entity) {
        WarrantyTypeResponseDTO dto = modelMapper.map(entity, WarrantyTypeResponseDTO.class);
        if (entity.getTicketType() != null) {
            dto.setTicketTypeId(entity.getTicketType().getTicketTypeId());
            dto.setTicketTypeName(entity.getTicketType().getTicketTypeName());
        }
        return dto;
    }
}
