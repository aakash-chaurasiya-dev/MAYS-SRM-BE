package com.mays.srm.controller;

import com.mays.srm.dto.requestDTO.BranchRequestDTO;
import com.mays.srm.dto.responseDTO.BranchResponseDTO;
import com.mays.srm.entity.Branch;
import com.mays.srm.service.BranchService;
import com.mays.srm.service.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/branches")
public class BranchController extends AbstractController<Branch, Integer> {

    @Autowired
    private BranchService branchService;

    @Override
    protected GenericService<Branch, Integer> getService() {
        return branchService;
    }

    @PostMapping
    public ResponseEntity<BranchResponseDTO> createBranch(@RequestBody BranchRequestDTO requestDTO) {
        BranchResponseDTO responseDTO = branchService.createBranch(requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BranchResponseDTO> getBranchById(@PathVariable Integer id) {
        BranchResponseDTO responseDTO = branchService.getBranchById(id);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<BranchResponseDTO>> getAllBranches() {
        List<BranchResponseDTO> responseDTOs = branchService.getAllBranches();
        return ResponseEntity.ok(responseDTOs);
    }
}
