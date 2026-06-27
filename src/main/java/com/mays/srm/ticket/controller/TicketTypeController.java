package com.mays.srm.ticket.controller;
import com.mays.srm.ticket.dto.request.TicketTypeRequestDTO;
import com.mays.srm.ticket.dto.resDTO.TicketTypeResponseDTO;
import com.mays.srm.ticket.service.TicketTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ticket-types")
public class TicketTypeController {

    @Autowired
    private TicketTypeService ticketTypeService;

    @PostMapping
    public ResponseEntity<TicketTypeResponseDTO> createTicketType(@RequestBody TicketTypeRequestDTO requestDTO) {
        TicketTypeResponseDTO responseDTO = ticketTypeService.create(requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketTypeResponseDTO> getTicketTypeById(@PathVariable Integer id) {
        TicketTypeResponseDTO responseDTO = ticketTypeService.getById(id);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<TicketTypeResponseDTO>> getAllTicketTypes() {
        List<TicketTypeResponseDTO> responseDTOs = ticketTypeService.getAll();
        return ResponseEntity.ok(responseDTOs);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TicketTypeResponseDTO> updateTicketType(@PathVariable Integer id, @RequestBody TicketTypeRequestDTO requestDTO) {
        TicketTypeResponseDTO updatedDto = ticketTypeService.update(id, requestDTO);
        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicketType(@PathVariable Integer id) {
        ticketTypeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
