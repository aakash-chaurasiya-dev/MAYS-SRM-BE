package com.mays.srm.ticket.repository.custom;
import com.mays.srm.ticket.entities.TicketLogs;
import com.mays.srm.ticket.dto.resDTO.TicketLogsSummaryResponseDTO;

import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public interface TicketLogsDaoCustom {
    List<TicketLogs> getDetailedTicketLogs(Integer ticketId);

    List<TicketLogsSummaryResponseDTO> getTicketLogsForTicketDetail(Integer ticketId);
}
