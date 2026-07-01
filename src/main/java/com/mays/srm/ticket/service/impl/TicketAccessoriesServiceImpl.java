package com.mays.srm.ticket.service.impl;

import com.mays.srm.device.repository.DeviceAccessoryMasterDao;
import com.mays.srm.device.entities.DeviceAccessoryMaster;
import com.mays.srm.ticket.repository.TicketAccessoriesDao;
import com.mays.srm.ticket.repository.TicketDao;
import com.mays.srm.ticket.dto.request.TicketAccessoriesRequestDTO;
import com.mays.srm.ticket.dto.resDTO.TicketAccessoriesResponseDTO;
import com.mays.srm.ticket.entities.Ticket;
import com.mays.srm.ticket.entities.TicketAccessories;
import com.mays.srm.ticket.service.TicketAccessoriesService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TicketAccessoriesServiceImpl implements TicketAccessoriesService {

    private final TicketAccessoriesDao dao;
    private final TicketDao ticketDao;
    private final DeviceAccessoryMasterDao accessoryDao;
    private final ModelMapper modelMapper;

    public TicketAccessoriesServiceImpl(TicketAccessoriesDao dao, TicketDao ticketDao, DeviceAccessoryMasterDao accessoryDao, ModelMapper modelMapper) {
        this.dao = dao;
        this.ticketDao = ticketDao;
        this.accessoryDao = accessoryDao;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public List<TicketAccessoriesResponseDTO> bulkCreate(List<TicketAccessoriesRequestDTO> requests) {
        List<TicketAccessories> entities = requests.stream().map(req -> {
            Ticket ticket = ticketDao.findById(req.getTicketId())
                    .orElseThrow(() -> new RuntimeException("Ticket not found with id: " + req.getTicketId()));
            DeviceAccessoryMaster accessory = accessoryDao.findById(req.getAccessoryId())
                    .orElseThrow(() -> new RuntimeException("Accessory not found with id: " + req.getAccessoryId()));
            
            TicketAccessories entity = new TicketAccessories();
            entity.setTicket(ticket);
            entity.setAccessory(accessory);
            return entity;
        }).collect(Collectors.toList());

        return dao.saveAll(entities).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<TicketAccessoriesResponseDTO> bulkUpdate(List<TicketAccessoriesRequestDTO> requests) {
        List<TicketAccessories> entities = requests.stream().map(req -> {
            TicketAccessories entity = dao.findById(req.getTicketAccessoriesId())
                    .orElseThrow(() -> new RuntimeException("TicketAccessories not found with id: " + req.getTicketAccessoriesId()));
            
            if (!entity.getTicket().getTicketId().equals(req.getTicketId())) {
                Ticket ticket = ticketDao.findById(req.getTicketId())
                        .orElseThrow(() -> new RuntimeException("Ticket not found with id: " + req.getTicketId()));
                entity.setTicket(ticket);
            }
            if (!entity.getAccessory().getAccessoryId().equals(req.getAccessoryId())) {
                DeviceAccessoryMaster accessory = accessoryDao.findById(req.getAccessoryId())
                        .orElseThrow(() -> new RuntimeException("Accessory not found with id: " + req.getAccessoryId()));
                entity.setAccessory(accessory);
            }
            return entity;
        }).collect(Collectors.toList());

        return dao.saveAll(entities).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TicketAccessoriesResponseDTO getById(Integer id) {
        TicketAccessories entity = dao.findById(id)
                .orElseThrow(() -> new RuntimeException("TicketAccessories not found with id: " + id));
        return mapToResponse(entity);
    }

    @Override
    public List<TicketAccessoriesResponseDTO> getAll() {
        return dao.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TicketAccessoriesResponseDTO> getByTicketId(Integer ticketId) {
        return dao.findByTicket_TicketId(ticketId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        TicketAccessories entity = dao.findById(id)
                .orElseThrow(() -> new RuntimeException("TicketAccessories not found with id: " + id));
        dao.delete(entity);
    }

    private TicketAccessoriesResponseDTO mapToResponse(TicketAccessories entity) {
        TicketAccessoriesResponseDTO responseDTO = modelMapper.map(entity, TicketAccessoriesResponseDTO.class);
        if (entity.getTicket() != null) {
            responseDTO.setTicketId(entity.getTicket().getTicketId());
        }
        if (entity.getAccessory() != null) {
            responseDTO.setAccessoryId(entity.getAccessory().getAccessoryId());
            responseDTO.setAccessoryName(entity.getAccessory().getAccessoryName());
        }
        return responseDTO;
    }
}
