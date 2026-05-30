package com.mays.srm.service;

import com.mays.srm.dto.responseDTO.TicketLogsResponseDTO;
import java.util.List;

public interface TicketLogsService {
    List<TicketLogsResponseDTO> getLogsForTicket(Integer ticketId);
}
