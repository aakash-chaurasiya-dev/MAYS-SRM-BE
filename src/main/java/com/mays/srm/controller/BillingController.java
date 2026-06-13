package com.mays.srm.controller;

import com.mays.srm.dto.requestDTO.BillingRequestDTO;
import com.mays.srm.dto.responseDTO.BillingResponseDTO;
import com.mays.srm.service.BillingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/billing")
public class BillingController {

    @Autowired
    private BillingService billingService;

    @PostMapping
    public ResponseEntity<BillingResponseDTO> createBilling(@RequestBody BillingRequestDTO requestDTO) {
        BillingResponseDTO responseDTO = billingService.create(requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BillingResponseDTO> getBillingById(@PathVariable Integer id) {
        BillingResponseDTO responseDTO = billingService.getById(id);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<BillingResponseDTO>> getAllBilling() {
        List<BillingResponseDTO> responseDTOs = billingService.getAll();
        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping("/final-charges")
    public ResponseEntity<List<BillingResponseDTO>> getFinalCharges() {
        List<BillingResponseDTO> responseDTOs = billingService.getFinalCharges();
        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<List<BillingResponseDTO>> getChargesByTicketId(@PathVariable Integer ticketId) {
        List<BillingResponseDTO> responseDTOs = billingService.getChargesByTicketId(ticketId);
        return ResponseEntity.ok(responseDTOs);
    }

    @PutMapping("/ticket/{ticketId}/charges")
    public ResponseEntity<Void> bulkUpdateCharges(@PathVariable Integer ticketId, @RequestBody List<BillingRequestDTO> requestDTOs) {
        billingService.bulkUpdateCharges(ticketId, requestDTOs);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<BillingResponseDTO> updateBilling(@PathVariable Integer id, @RequestBody BillingRequestDTO requestDTO) {
        BillingResponseDTO updatedDto = billingService.update(id, requestDTO);
        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBilling(@PathVariable Integer id) {
        billingService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
