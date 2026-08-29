package com.mays.srm.inventory.controller;

import com.mays.srm.inventory.dto.request.TicketPartApproveRequestDTO;
import com.mays.srm.inventory.dto.request.TicketPartRequestDTO;
import com.mays.srm.inventory.dto.resDTO.TicketPartResponseDTO;
import com.mays.srm.inventory.service.TicketPartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ticket-parts")
public class TicketPartController {

    private final TicketPartService ticketPartService;

    public TicketPartController(TicketPartService ticketPartService) {
        this.ticketPartService = ticketPartService;
    }

    @GetMapping
    public ResponseEntity<List<TicketPartResponseDTO>> getAll() {
        return ResponseEntity.ok(ticketPartService.getAll());
    }

    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<List<TicketPartResponseDTO>> getByTicketId(@PathVariable Integer ticketId) {
        return ResponseEntity.ok(ticketPartService.getByTicketId(ticketId));
    }

    @PostMapping
    public ResponseEntity<TicketPartResponseDTO> create(@RequestBody TicketPartRequestDTO request) {
        return new ResponseEntity<>(ticketPartService.create(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TicketPartResponseDTO> update(
            @PathVariable Integer id,
            @RequestBody TicketPartRequestDTO request) {
        return ResponseEntity.ok(ticketPartService.update(id, request));
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<TicketPartResponseDTO> approve(
            @PathVariable Integer id,
            @RequestBody TicketPartApproveRequestDTO request) {
        return ResponseEntity.ok(ticketPartService.approve(id, request));
    }

    @PatchMapping("/{id}/customer-approve")
    public ResponseEntity<TicketPartResponseDTO> customerApprove(
            @PathVariable Integer id,
            @RequestBody TicketPartApproveRequestDTO request) {
        return ResponseEntity.ok(ticketPartService.customerApprove(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        ticketPartService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
