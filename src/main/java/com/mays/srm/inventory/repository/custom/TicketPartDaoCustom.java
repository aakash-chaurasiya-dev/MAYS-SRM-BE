package com.mays.srm.inventory.repository.custom;

import com.mays.srm.inventory.dto.resDTO.TicketPartResponseDTO;

import java.util.List;

public interface TicketPartDaoCustom {

    List<TicketPartResponseDTO> findDetailsByTicketId(Integer ticketId);

    List<TicketPartResponseDTO> findAllDetails();
}
