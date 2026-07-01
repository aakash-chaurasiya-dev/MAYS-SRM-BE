package com.mays.srm.ticket.service;
import com.mays.srm.ticket.dto.resDTO.TicketLogsResponseDTO;
import com.mays.srm.ticket.dto.resDTO.TicketLogsSummaryResponseDTO;
import java.util.List;

public interface TicketLogsService {
    List<TicketLogsResponseDTO> getLogsForTicket(Integer ticketId);
    List<TicketLogsSummaryResponseDTO> getLatestLogsForTicket(Integer ticketId);
}
