package com.mays.srm.ticket.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mays.srm.ticket.dto.resDTO.TicketTimeTrackingResponseDTO;
import com.mays.srm.ticket.dto.resDTO.EmployeeTicketStatsDTO;
import com.mays.srm.ticket.dto.resDTO.EmployeeTicketHistoryDTO;
import com.mays.srm.ticket.service.TicketTimeTrackingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/ticket-time-tracking")
public class TicketTimeTrackingController {

    @Autowired
    private TicketTimeTrackingService trackingService;

    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<List<TicketTimeTrackingResponseDTO>> getTrackingByTicketId(@PathVariable Integer ticketId) {
        List<TicketTimeTrackingResponseDTO> records = trackingService.getTimeTrackingByTicketId(ticketId);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketTimeTrackingResponseDTO> getTrackingById(@PathVariable Long id) {
        TicketTimeTrackingResponseDTO record = trackingService.getTrackingById(id);
        return ResponseEntity.ok(record);
    }
    
    @GetMapping("/employee/{employeeId}/stats")
    public ResponseEntity<EmployeeTicketStatsDTO> getEmployeeTicketStats(@PathVariable Integer employeeId) {
        return ResponseEntity.ok(trackingService.getEmployeeTicketStats(employeeId));
    }
    
    @GetMapping("/employee/{employeeId}/history")
    public ResponseEntity<Page<EmployeeTicketHistoryDTO>> getEmployeeTicketHistory(
            @PathVariable Integer employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(trackingService.getEmployeeTicketHistory(employeeId, PageRequest.of(page, size)));
    }
}
