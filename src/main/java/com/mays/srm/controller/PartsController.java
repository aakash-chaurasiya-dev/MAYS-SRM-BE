package com.mays.srm.controller;

import com.mays.srm.dto.requestDTO.PartsRequestDTO;
import com.mays.srm.dto.responseDTO.PartsResponseDTO;
import com.mays.srm.service.PartsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parts")
public class PartsController {

    @Autowired
    private PartsService partsService;

    @PostMapping
    public ResponseEntity<PartsResponseDTO> createPart(@RequestBody PartsRequestDTO requestDTO) {
        PartsResponseDTO responseDTO = partsService.create(requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartsResponseDTO> getPartById(@PathVariable Integer id) {
        PartsResponseDTO responseDTO = partsService.getById(id);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<PartsResponseDTO>> getAllParts() {
        List<PartsResponseDTO> responseDTOs = partsService.getAll();
        return ResponseEntity.ok(responseDTOs);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PartsResponseDTO> updatePart(@PathVariable Integer id, @RequestBody PartsRequestDTO requestDTO) {
        PartsResponseDTO updatedDto = partsService.update(id, requestDTO);
        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePart(@PathVariable Integer id) {
        partsService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
