package com.mays.srm.ticket.controller;

import com.mays.srm.ticket.dto.request.ReferredCategoryRequestDTO;
import com.mays.srm.ticket.dto.resDTO.ReferredCategoryResponseDTO;
import com.mays.srm.ticket.service.ReferredCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/referred-categories")
public class ReferredCategoryController {

    @Autowired
    private ReferredCategoryService service;

    @PostMapping
    public ResponseEntity<ReferredCategoryResponseDTO> create(@RequestBody ReferredCategoryRequestDTO requestDTO) {
        return ResponseEntity.ok(service.create(requestDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReferredCategoryResponseDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<ReferredCategoryResponseDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReferredCategoryResponseDTO> update(@PathVariable Integer id, @RequestBody ReferredCategoryRequestDTO requestDTO) {
        return ResponseEntity.ok(service.update(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
