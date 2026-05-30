package com.mays.srm.controller;

import com.mays.srm.dto.responseDTO.TicketLogsResponseDTO;
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

    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<List<TicketLogsResponseDTO>> getLogsForTicket(@PathVariable Integer ticketId) {
        List<TicketLogsResponseDTO> responseDTOs = ticketLogsService.getLogsForTicket(ticketId);
        return ResponseEntity.ok(responseDTOs);
    }
}
