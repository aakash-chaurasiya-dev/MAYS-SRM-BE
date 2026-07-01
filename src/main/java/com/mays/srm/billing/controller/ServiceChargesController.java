package com.mays.srm.billing.controller;
import com.mays.srm.billing.dto.request.ServiceChargesRequestDTO;
import com.mays.srm.billing.dto.resDTO.ServiceChargesResponseDTO;
import com.mays.srm.billing.service.ServiceChargesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/service-charges")
public class ServiceChargesController {

    @Autowired
    private ServiceChargesService serviceChargesService;

    @PostMapping
    public ResponseEntity<ServiceChargesResponseDTO> createServiceCharge(@RequestBody ServiceChargesRequestDTO requestDTO) {
        ServiceChargesResponseDTO responseDTO = serviceChargesService.create(requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceChargesResponseDTO> getServiceChargeById(@PathVariable Integer id) {
        ServiceChargesResponseDTO responseDTO = serviceChargesService.getById(id);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<ServiceChargesResponseDTO>> getAllServiceCharges() {
        List<ServiceChargesResponseDTO> responseDTOs = serviceChargesService.getAll();
        return ResponseEntity.ok(responseDTOs);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceChargesResponseDTO> updateServiceCharge(@PathVariable Integer id, @RequestBody ServiceChargesRequestDTO requestDTO) {
        ServiceChargesResponseDTO updatedDto = serviceChargesService.update(id, requestDTO);
        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteServiceCharge(@PathVariable Integer id) {
        serviceChargesService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
