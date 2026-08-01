package com.mays.srm.ticket.controller;

import com.mays.srm.ticket.dto.request.WarrantyTypeRequestDTO;
import com.mays.srm.ticket.dto.resDTO.WarrantyTypeResponseDTO;
import com.mays.srm.ticket.service.WarrantyTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warranty-types")
public class WarrantyTypeController {

    @Autowired
    private WarrantyTypeService service;

    @PostMapping
    public ResponseEntity<WarrantyTypeResponseDTO> create(@RequestBody WarrantyTypeRequestDTO requestDTO) {
        return ResponseEntity.ok(service.create(requestDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WarrantyTypeResponseDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<WarrantyTypeResponseDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<WarrantyTypeResponseDTO> update(@PathVariable Integer id, @RequestBody WarrantyTypeRequestDTO requestDTO) {
        return ResponseEntity.ok(service.update(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
