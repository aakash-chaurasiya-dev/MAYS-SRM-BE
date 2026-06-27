package com.mays.srm.service;

import com.mays.srm.dto.responseDTO.TicketLogsDTO.TicketLogsResponseDTO;
import com.mays.srm.dto.responseDTO.TicketLogsDTO.TicketLogsSummaryResponseDTO;
import java.util.List;

public interface TicketLogsService {
    List<TicketLogsResponseDTO> getLogsForTicket(Integer ticketId);
    List<TicketLogsSummaryResponseDTO> getLatestLogsForTicket(Integer ticketId);
}
