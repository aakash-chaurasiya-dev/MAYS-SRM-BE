package com.mays.srm.inventory.controller;

import com.mays.srm.inventory.dto.request.InStockPartRequestDTO;
import com.mays.srm.inventory.dto.resDTO.InStockPartOptionDTO;
import com.mays.srm.inventory.dto.resDTO.InStockPartResponseDTO;
import com.mays.srm.inventory.service.InStockPartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory/in-stock")
public class InStockPartController {

    private final InStockPartService inStockPartService;

    public InStockPartController(InStockPartService inStockPartService) {
        this.inStockPartService = inStockPartService;
    }

    @GetMapping("/available")
    public ResponseEntity<List<InStockPartOptionDTO>> findAvailable(
            @RequestParam Integer partCatId) {
        return ResponseEntity.ok(inStockPartService.findAvailable(partCatId));
    }

    @GetMapping
    public ResponseEntity<List<InStockPartResponseDTO>> getAll(
            @RequestParam(required = false) Integer partCatId) {
        return ResponseEntity.ok(inStockPartService.getAll(partCatId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InStockPartResponseDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(inStockPartService.getById(id));
    }

    @PostMapping
    public ResponseEntity<InStockPartResponseDTO> create(@RequestBody InStockPartRequestDTO request) {
        return new ResponseEntity<>(inStockPartService.create(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InStockPartResponseDTO> update(
            @PathVariable Integer id,
            @RequestBody InStockPartRequestDTO request) {
        return ResponseEntity.ok(inStockPartService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        inStockPartService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteBulk(@RequestBody List<Integer> ids) {
        inStockPartService.deleteBulk(ids);
        return ResponseEntity.noContent().build();
    }
}
