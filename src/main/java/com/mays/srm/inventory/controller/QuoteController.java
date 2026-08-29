package com.mays.srm.inventory.controller;

import com.mays.srm.inventory.dto.request.QuoteRequestDTO;
import com.mays.srm.inventory.dto.resDTO.QuoteResponseDTO;
import com.mays.srm.inventory.service.QuoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quotes")
public class QuoteController {

    private final QuoteService quoteService;

    public QuoteController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @GetMapping("/ticket-part/{ticketPartId}")
    public ResponseEntity<List<QuoteResponseDTO>> getByTicketPartId(@PathVariable Integer ticketPartId) {
        return ResponseEntity.ok(quoteService.getByTicketPartId(ticketPartId));
    }

    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<List<QuoteResponseDTO>> getByTicketId(@PathVariable Integer ticketId) {
        return ResponseEntity.ok(quoteService.getByTicketId(ticketId));
    }

    @PostMapping
    public ResponseEntity<QuoteResponseDTO> create(@RequestBody QuoteRequestDTO request) {
        return new ResponseEntity<>(quoteService.create(request), HttpStatus.CREATED);
    }

    @PutMapping("/{quoteId}")
    public ResponseEntity<QuoteResponseDTO> update(
            @PathVariable Integer quoteId,
            @RequestBody QuoteRequestDTO request) {
        return ResponseEntity.ok(quoteService.update(quoteId, request));
    }

    @PatchMapping("/{quoteId}/send")
    public ResponseEntity<QuoteResponseDTO> send(@PathVariable Integer quoteId) {
        return ResponseEntity.ok(quoteService.send(quoteId));
    }
}
