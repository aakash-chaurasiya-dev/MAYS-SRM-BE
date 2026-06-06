package com.mays.srm.controller;

import com.mays.srm.dto.requestDTO.StatusRequestDTO;
import com.mays.srm.dto.responseDTO.StatusResponseDTO;
import com.mays.srm.service.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statuses")
public class StatusController {

    @Autowired
    private StatusService statusService;

    @PostMapping
    public ResponseEntity<StatusResponseDTO> createStatus(@RequestBody StatusRequestDTO requestDTO) {
        StatusResponseDTO responseDTO = statusService.create(requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StatusResponseDTO> getStatusById(@PathVariable Integer id) {
        StatusResponseDTO responseDTO = statusService.getById(id);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<StatusResponseDTO>> getAllStatuses() {
        List<StatusResponseDTO> responseDTOs = statusService.getAll();
        return ResponseEntity.ok(responseDTOs);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StatusResponseDTO> updateStatus(@PathVariable Integer id, @RequestBody StatusRequestDTO requestDTO) {
        StatusResponseDTO updatedDto = statusService.update(id, requestDTO);
        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStatus(@PathVariable Integer id) {
        statusService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/type/{statusType}")
    public ResponseEntity<List<StatusResponseDTO>> getStatusesByType(@PathVariable String statusType) {
        List<StatusResponseDTO> responseDTOs = statusService.getStatusesByType(statusType);
        return ResponseEntity.ok(responseDTOs);
    }
}
