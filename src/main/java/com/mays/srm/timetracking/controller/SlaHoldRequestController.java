package com.mays.srm.timetracking.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mays.srm.timetracking.dto.resDTO.SlaHoldRequestResponseDTO;
import com.mays.srm.timetracking.service.SlaHoldRequestService;

@RestController
@RequestMapping("/api/sla-hold-requests")
public class SlaHoldRequestController {

    @Autowired
    private SlaHoldRequestService holdRequestService;

    @GetMapping("/pending")
    public ResponseEntity<List<SlaHoldRequestResponseDTO>> getPending() {
        return ResponseEntity.ok(holdRequestService.getPendingRequests());
    }

    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<List<SlaHoldRequestResponseDTO>> getByTicket(@PathVariable Integer ticketId) {
        return ResponseEntity.ok(holdRequestService.getByTicketId(ticketId));
    }

    @GetMapping("/ticket/{ticketId}/active")
    public ResponseEntity<SlaHoldRequestResponseDTO> getActiveForTicket(@PathVariable Integer ticketId) {
        SlaHoldRequestResponseDTO active = holdRequestService.getActiveForTicket(ticketId);
        if (active == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(active);
    }
}
