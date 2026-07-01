package com.mays.srm.ticket.controller;

import com.mays.srm.ticket.dto.request.TicketAccessoriesRequestDTO;
import com.mays.srm.ticket.dto.resDTO.TicketAccessoriesResponseDTO;
import com.mays.srm.ticket.service.TicketAccessoriesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ticket-accessories")
public class TicketAccessoriesController {

    private final TicketAccessoriesService service;

    public TicketAccessoriesController(TicketAccessoriesService service) {
        this.service = service;
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<TicketAccessoriesResponseDTO>> bulkCreate(@RequestBody List<TicketAccessoriesRequestDTO> requests) {
        return new ResponseEntity<>(service.bulkCreate(requests), HttpStatus.CREATED);
    }

    @PutMapping("/bulk")
    public ResponseEntity<List<TicketAccessoriesResponseDTO>> bulkUpdate(@RequestBody List<TicketAccessoriesRequestDTO> requests) {
        return ResponseEntity.ok(service.bulkUpdate(requests));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketAccessoriesResponseDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<TicketAccessoriesResponseDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<List<TicketAccessoriesResponseDTO>> getByTicketId(@PathVariable Integer ticketId) {
        return ResponseEntity.ok(service.getByTicketId(ticketId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
