package com.mays.srm.controller;

import com.mays.srm.dto.requestDTO.BranchRequestDTO;
import com.mays.srm.dto.responseDTO.BranchResponseDTO;
import com.mays.srm.service.BranchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/branches")
public class BranchController {

    @Autowired
    private BranchService branchService;

    @PostMapping
    public ResponseEntity<BranchResponseDTO> createBranch(@RequestBody BranchRequestDTO requestDTO) {
        BranchResponseDTO responseDTO = branchService.create(requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BranchResponseDTO> getBranchById(@PathVariable Integer id) {
        BranchResponseDTO responseDTO = branchService.getById(id);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<BranchResponseDTO>> getAllBranches() {
        List<BranchResponseDTO> responseDTOs = branchService.getAll();
        return ResponseEntity.ok(responseDTOs);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BranchResponseDTO> updateBranch(@PathVariable Integer id, @RequestBody BranchRequestDTO requestDTO) {
        // This logic will be implemented in the service layer next
        BranchResponseDTO updatedDto = branchService.update(id, requestDTO);
        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBranch(@PathVariable Integer id) {
        branchService.delete(id); // The generic delete is fine for this
        return ResponseEntity.noContent().build();
    }
}
