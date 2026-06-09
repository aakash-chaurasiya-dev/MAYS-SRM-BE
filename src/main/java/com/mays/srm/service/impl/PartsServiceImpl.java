package com.mays.srm.service.impl;

import com.mays.srm.dao.core.InventoryDao;
import com.mays.srm.dao.core.PartsDao;
import com.mays.srm.dao.core.StatusDao;
import com.mays.srm.dao.core.TicketDao;
import com.mays.srm.dto.requestDTO.PartsRequestDTO;
import com.mays.srm.dto.responseDTO.PartsResponseDTO;
import com.mays.srm.entity.Inventory;
import com.mays.srm.entity.Parts;
import com.mays.srm.entity.Status;
import com.mays.srm.entity.Ticket;
import com.mays.srm.exception.BadRequestException;
import com.mays.srm.exception.InternalServerException;
import com.mays.srm.exception.ResourceNotFoundException;
import com.mays.srm.service.PartsService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PartsServiceImpl implements PartsService {

    private final PartsDao repository;
    private final TicketDao ticketDao;
    private final InventoryDao inventoryDao;
    private final StatusDao statusDao;
    private final ModelMapper modelMapper;

    @Autowired
    public PartsServiceImpl(PartsDao repository, TicketDao ticketDao, InventoryDao inventoryDao, StatusDao statusDao, ModelMapper modelMapper) {
        this.repository = repository;
        this.ticketDao = ticketDao;
        this.inventoryDao = inventoryDao;
        this.statusDao = statusDao;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public PartsResponseDTO create(PartsRequestDTO requestDTO) {
        try {
            Parts part = modelMapper.map(requestDTO, Parts.class);
            
            // Validate and set basic relations first
            validateAndSetRelations(part, requestDTO);

            // Handle inventory logic
            if (part.getTicket() != null) {
                Inventory inventoryItem = part.getProduct();
                int requestedQuantity = part.getQuantity();

                if (inventoryItem.getStock() >= requestedQuantity) {
                    // Reduce stock and save inventory
                    inventoryItem.setStock(inventoryItem.getStock() - requestedQuantity);
                    inventoryDao.save(inventoryItem);
                    
                    // Mark the part as being in stock
                    part.setInStock(true);
                    //when i got the parts instock will mark status as received.
                    Status status = statusDao.getReferenceById(12);
                    part.setStatus(status);
                }
            }
            Parts savedPart = repository.save(part);
            return mapToResponseDTO(savedPart);
        } catch (ResourceNotFoundException | DataIntegrityViolationException | BadRequestException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while creating Part", ex);
        }
    }

    @Override
    public PartsResponseDTO getById(Integer id) {
        Optional<Parts> partOpt = repository.findById(id);
        if (partOpt.isPresent()) {
            return mapToResponseDTO(partOpt.get());
        } else {
            throw new ResourceNotFoundException("Part not found with ID: " + id);
        }
    }

    @Override
    public List<PartsResponseDTO> getAll() {
        List<Parts> partsList = repository.findAll();
        List<PartsResponseDTO> dtoList = new ArrayList<>();
        for (Parts part : partsList) {
            dtoList.add(mapToResponseDTO(part));
        }
        return dtoList;
    }

    @Override
    @Transactional
    public PartsResponseDTO update(Integer id, PartsRequestDTO requestDTO) {
        Optional<Parts> existingOpt = repository.findById(id);
        if (existingOpt.isEmpty()) {
            throw new ResourceNotFoundException("Cannot update. Part not found with ID: " + id);
        }
        
        Parts existingPart = existingOpt.get();

        // Note: Updating inventory stock on a part update can be complex.
        // For example, if the quantity changes. This implementation does not handle that.
        // It's generally safer to handle stock changes on creation or through a separate return process.

        modelMapper.map(requestDTO, existingPart);
        existingPart.setPartId(id);

        try {
            validateAndSetRelations(existingPart, requestDTO);
            Parts updatedPart = repository.save(existingPart);
            return mapToResponseDTO(updatedPart);
        } catch (ResourceNotFoundException | DataIntegrityViolationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while updating Part", ex);
        }
    }

    @Override
    public void delete(Integer id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete. Part not found with ID: " + id);
        }
        try {
            repository.deleteById(id);
        } catch (Exception ex) {
            throw new InternalServerException("Error occurred while deleting Part with ID: " + id, ex);
        }
    }

    private void validateAndSetRelations(Parts part, PartsRequestDTO requestDTO) {
        if (requestDTO.getTicketId() != null) {
            Optional<Ticket> ticketOpt = ticketDao.findById(requestDTO.getTicketId());
            if (ticketOpt.isPresent()) {
                part.setTicket(ticketOpt.get());
            } else {
                throw new ResourceNotFoundException("Ticket not found with ID: " + requestDTO.getTicketId());
            }
        }

        if (requestDTO.getProductId() != null) {
            Optional<Inventory> inventoryOpt = inventoryDao.findById(requestDTO.getProductId());
            if (inventoryOpt.isPresent()) {
                part.setProduct(inventoryOpt.get());
            } else {
                throw new ResourceNotFoundException("Product not found with ID: " + requestDTO.getProductId());
            }
        } else {
             part.setProduct(null);
        }

        if (requestDTO.getStatusId() != null) {
            Optional<Status> statusOpt = statusDao.findById(requestDTO.getStatusId());
            if (statusOpt.isPresent()) {
                if ("PARTS".equalsIgnoreCase(statusOpt.get().getStatusType())) {
                    part.setStatus(statusOpt.get());
                } else {
                    throw new ResourceNotFoundException("Status has to be of type parts: " + requestDTO.getStatusId());
                }
            } else {
                throw new ResourceNotFoundException("Status not found with ID: " + requestDTO.getStatusId());
            }
        }
    }

    private PartsResponseDTO mapToResponseDTO(Parts part) {
        PartsResponseDTO dto = modelMapper.map(part, PartsResponseDTO.class);
        if (part.getTicket() != null) {
            dto.setTicketId(part.getTicket().getTicketId());
        }
        if (part.getProduct() != null) {
            dto.setProductName(part.getProduct().getProductName());
        }
        if (part.getStatus() != null) {
            dto.setStatusName(part.getStatus().getStatusName());
        }
        return dto;
    }
}
