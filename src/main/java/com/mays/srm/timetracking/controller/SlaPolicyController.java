package com.mays.srm.timetracking.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mays.srm.timetracking.dto.request.SlaPolicyRequestDTO;
import com.mays.srm.timetracking.dto.resDTO.SlaPolicyResponseDTO;
import com.mays.srm.timetracking.service.SlaPolicyService;

@RestController
@RequestMapping("/api/sla-policies")
public class SlaPolicyController {

    @Autowired
    private SlaPolicyService slaPolicyService;

    @GetMapping
    public ResponseEntity<List<SlaPolicyResponseDTO>> getAll() {
        return ResponseEntity.ok(slaPolicyService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SlaPolicyResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(slaPolicyService.getById(id));
    }

    @PostMapping
    public ResponseEntity<SlaPolicyResponseDTO> create(@RequestBody SlaPolicyRequestDTO requestDTO) {
        return ResponseEntity.ok(slaPolicyService.create(requestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SlaPolicyResponseDTO> update(@PathVariable Long id, @RequestBody SlaPolicyRequestDTO requestDTO) {
        return ResponseEntity.ok(slaPolicyService.update(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        slaPolicyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
