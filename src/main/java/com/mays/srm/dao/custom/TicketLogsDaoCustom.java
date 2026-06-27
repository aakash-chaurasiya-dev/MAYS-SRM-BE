package com.mays.srm.dao.custom;

import com.mays.srm.entity.TicketLogs;
import com.mays.srm.dto.responseDTO.TicketLogsDTO.TicketLogsSummaryResponseDTO;

import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public interface TicketLogsDaoCustom {
    List<TicketLogs> getDetailedTicketLogs(Integer ticketId);

    List<TicketLogsSummaryResponseDTO> getTicketLogsForTicketDetail(Integer ticketId);
}
