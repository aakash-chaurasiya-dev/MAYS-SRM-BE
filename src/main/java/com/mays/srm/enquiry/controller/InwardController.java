package com.mays.srm.enquiry.controller;

import com.mays.srm.enquiry.dto.request.InwardRequestDTO;
import com.mays.srm.enquiry.dto.resDTO.InwardResponseDTO;
import com.mays.srm.enquiry.service.InwardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/inwards")
public class InwardController {

    @Autowired
    private InwardService inwardService;

    @PostMapping
    public ResponseEntity<InwardResponseDTO> createInward(@RequestBody InwardRequestDTO requestDTO) {
        return ResponseEntity.ok(inwardService.createInward(requestDTO));
    }

    @GetMapping
    public ResponseEntity<List<InwardResponseDTO>> getAllInwards() {
        return ResponseEntity.ok(inwardService.getAllInwards());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InwardResponseDTO> getInwardById(@PathVariable Integer id) {
        return ResponseEntity.ok(inwardService.getInwardById(id));
    }
}
