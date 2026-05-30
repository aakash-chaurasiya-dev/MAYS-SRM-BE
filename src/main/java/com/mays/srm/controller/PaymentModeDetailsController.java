package com.mays.srm.controller;

import com.mays.srm.dto.requestDTO.PaymentModeDetailsRequestDTO;
import com.mays.srm.dto.responseDTO.PaymentModeDetailsResponseDTO;
import com.mays.srm.service.PaymentModeDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment-modes")
public class PaymentModeDetailsController {

    @Autowired
    private PaymentModeDetailsService paymentModeDetailsService;

    @PostMapping
    public ResponseEntity<PaymentModeDetailsResponseDTO> createPaymentMode(@RequestBody PaymentModeDetailsRequestDTO requestDTO) {
        PaymentModeDetailsResponseDTO responseDTO = paymentModeDetailsService.create(requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentModeDetailsResponseDTO> getPaymentModeById(@PathVariable Integer id) {
        PaymentModeDetailsResponseDTO responseDTO = paymentModeDetailsService.getById(id);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<PaymentModeDetailsResponseDTO>> getAllPaymentModes() {
        List<PaymentModeDetailsResponseDTO> responseDTOs = paymentModeDetailsService.getAll();
        return ResponseEntity.ok(responseDTOs);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentModeDetailsResponseDTO> updatePaymentMode(@PathVariable Integer id, @RequestBody PaymentModeDetailsRequestDTO requestDTO) {
        PaymentModeDetailsResponseDTO updatedDto = paymentModeDetailsService.update(id, requestDTO);
        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaymentMode(@PathVariable Integer id) {
        paymentModeDetailsService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
