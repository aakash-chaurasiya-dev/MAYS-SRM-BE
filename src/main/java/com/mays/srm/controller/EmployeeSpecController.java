package com.mays.srm.controller;

import com.mays.srm.dto.requestDTO.EmployeeSpecRequestDTO;
import com.mays.srm.dto.responseDTO.EmployeeSpecResponseDTO;
import com.mays.srm.service.EmployeeSpecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee-specs")
public class EmployeeSpecController {

    @Autowired
    private EmployeeSpecService employeeSpecService;

    @PostMapping
    public ResponseEntity<EmployeeSpecResponseDTO> createEmployeeSpec(@RequestBody EmployeeSpecRequestDTO requestDTO) {
        EmployeeSpecResponseDTO responseDTO = employeeSpecService.create(requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<EmployeeSpecResponseDTO>> getAllEmployeeSpecs() {
        List<EmployeeSpecResponseDTO> responseDTOs = employeeSpecService.getAll();
        return ResponseEntity.ok(responseDTOs);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteEmployeeSpec(@RequestParam Integer employeeId, @RequestParam Integer deviceTypeId) {
        employeeSpecService.delete(employeeId, deviceTypeId);
        return ResponseEntity.noContent().build();
    }
}
