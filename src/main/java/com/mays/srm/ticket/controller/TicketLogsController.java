package com.mays.srm.ticket.controller;
import com.mays.srm.ticket.dto.resDTO.TicketLogsResponseDTO;
import com.mays.srm.ticket.dto.resDTO.TicketLogsSummaryResponseDTO;
import com.mays.srm.service.TicketLogsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ticket-logs")
public class TicketLogsController {

    @Autowired
    private TicketLogsService ticketLogsService;

    @GetMapping("/{ticketId}")
    public ResponseEntity<List<TicketLogsResponseDTO>> getLogsForTicket(@PathVariable Integer ticketId) {
        List<TicketLogsResponseDTO> responseDTOs = ticketLogsService.getLogsForTicket(ticketId);
        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping("/{ticketId}/latest")
    public ResponseEntity<List<TicketLogsSummaryResponseDTO>> getLatestLogsForTicket(@PathVariable Integer ticketId) {
        List<TicketLogsSummaryResponseDTO> responseDTOs = ticketLogsService.getLatestLogsForTicket(ticketId);
        return ResponseEntity.ok(responseDTOs);
    }
}
