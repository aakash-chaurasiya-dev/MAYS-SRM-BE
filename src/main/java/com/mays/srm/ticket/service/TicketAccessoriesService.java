package com.mays.srm.ticket.service;

import com.mays.srm.ticket.dto.request.TicketAccessoriesRequestDTO;
import com.mays.srm.ticket.dto.resDTO.TicketAccessoriesResponseDTO;

import java.util.List;

public interface TicketAccessoriesService {
    TicketAccessoriesResponseDTO getById(Integer id);
    List<TicketAccessoriesResponseDTO> getAll();
    void delete(Integer id);
    
    List<TicketAccessoriesResponseDTO> getByTicketId(Integer ticketId);
    List<TicketAccessoriesResponseDTO> bulkCreate(List<TicketAccessoriesRequestDTO> requests);
    List<TicketAccessoriesResponseDTO> bulkUpdate(List<TicketAccessoriesRequestDTO> requests);
}
