package com.mays.srm.inventory.controller;

import com.mays.srm.inventory.dto.request.PartPriceCombinedRequestDTO;
import com.mays.srm.inventory.dto.resDTO.PartPriceCombinedResponseDTO;
import com.mays.srm.inventory.service.PartPriceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory/prices")
public class PartPriceController {

    private final PartPriceService partPriceService;

    public PartPriceController(PartPriceService partPriceService) {
        this.partPriceService = partPriceService;
    }

    @GetMapping
    public ResponseEntity<List<PartPriceCombinedResponseDTO>> getAll() {
        return ResponseEntity.ok(partPriceService.getAll());
    }

    @GetMapping("/{partCatId}")
    public ResponseEntity<PartPriceCombinedResponseDTO> getByPartCatId(@PathVariable Integer partCatId) {
        return ResponseEntity.ok(partPriceService.getByPartCatId(partCatId));
    }

    @PostMapping
    public ResponseEntity<PartPriceCombinedResponseDTO> create(
            @RequestBody PartPriceCombinedRequestDTO request) {
        return new ResponseEntity<>(partPriceService.upsert(request), HttpStatus.CREATED);
    }

    @PutMapping("/{partCatId}")
    public ResponseEntity<PartPriceCombinedResponseDTO> update(
            @PathVariable Integer partCatId,
            @RequestBody PartPriceCombinedRequestDTO request) {
        return ResponseEntity.ok(partPriceService.update(partCatId, request));
    }

    @DeleteMapping("/{partCatId}")
    public ResponseEntity<Void> delete(@PathVariable Integer partCatId) {
        partPriceService.delete(partCatId);
        return ResponseEntity.noContent().build();
    }
}
