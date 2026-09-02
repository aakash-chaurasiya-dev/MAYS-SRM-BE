package com.mays.srm.enquiry.controller;

import com.mays.srm.enquiry.dto.request.OutwardRequestDTO;
import com.mays.srm.enquiry.dto.resDTO.OutwardResponseDTO;
import com.mays.srm.enquiry.service.OutwardService;
import com.mays.srm.ticket.dto.resDTO.TicketResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/outwards")
public class OutwardController {

    @Autowired
    private OutwardService outwardService;

    @PostMapping
    public ResponseEntity<OutwardResponseDTO> createOutward(@RequestBody OutwardRequestDTO requestDTO) {
        return ResponseEntity.ok(outwardService.createOutward(requestDTO));
    }

    @GetMapping
    public ResponseEntity<List<OutwardResponseDTO>> getAllOutwards() {
        return ResponseEntity.ok(outwardService.getAllOutwards());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OutwardResponseDTO> getOutwardById(@PathVariable Integer id) {
        return ResponseEntity.ok(outwardService.getOutwardById(id));
    }

    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<OutwardResponseDTO> getOutwardByTicketId(@PathVariable Integer ticketId) {
        return ResponseEntity.ok(outwardService.getOutwardByTicketId(ticketId));
    }

    @GetMapping("/user/{userId}/eligible")
    public ResponseEntity<List<TicketResponseDTO>> getEligibleTicketsForUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(outwardService.getEligibleTicketsForUser(userId));
    }
}
