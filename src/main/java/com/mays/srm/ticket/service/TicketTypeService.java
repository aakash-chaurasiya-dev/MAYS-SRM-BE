package com.mays.srm.ticket.service;
import com.mays.srm.service.GenericService;
import com.mays.srm.ticket.dto.request.TicketTypeRequestDTO;
import com.mays.srm.ticket.dto.resDTO.TicketTypeResponseDTO;

public interface TicketTypeService extends GenericService<TicketTypeRequestDTO, TicketTypeResponseDTO, Integer> {
}
