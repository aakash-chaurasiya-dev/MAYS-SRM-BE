package com.mays.srm.inventory.service;

import com.mays.srm.inventory.dto.request.TicketPartApproveRequestDTO;
import com.mays.srm.inventory.dto.request.TicketPartRequestDTO;
import com.mays.srm.inventory.dto.resDTO.TicketPartResponseDTO;

import java.util.List;

public interface TicketPartService {

    List<TicketPartResponseDTO> getAll();

    List<TicketPartResponseDTO> getByTicketId(Integer ticketId);

    TicketPartResponseDTO create(TicketPartRequestDTO request);

    TicketPartResponseDTO update(Integer ticketPartId, TicketPartRequestDTO request);

    TicketPartResponseDTO approve(Integer ticketPartId, TicketPartApproveRequestDTO request);

    TicketPartResponseDTO customerApprove(Integer ticketPartId, TicketPartApproveRequestDTO request);

    void delete(Integer ticketPartId);
}
