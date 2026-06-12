package com.mays.srm.controller;

import com.mays.srm.dto.requestDTO.EmployeeRequestDTO;
import com.mays.srm.dto.responseDTO.EmployeeResponseDTO;
import com.mays.srm.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<EmployeeResponseDTO> createEmployee(@RequestBody EmployeeRequestDTO requestDTO) {
        EmployeeResponseDTO responseDTO = employeeService.create(requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> getEmployeeById(@PathVariable Integer id) {
        EmployeeResponseDTO responseDTO = employeeService.getById(id);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<EmployeeResponseDTO>> getAllEmployees() {
        List<EmployeeResponseDTO> responseDTOs = employeeService.getAll();
        return ResponseEntity.ok(responseDTOs);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> updateEmployee(@PathVariable Integer id, @RequestBody EmployeeRequestDTO requestDTO) {
        EmployeeResponseDTO updatedDto = employeeService.update(id, requestDTO);
        return ResponseEntity.ok(updatedDto);
    }

    // Existing DELETE for single record by path variable
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Integer id) {
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // New DELETE for multiple records by request body
    @DeleteMapping
    public ResponseEntity<Void> deleteEmployees(@RequestBody List<Integer> ids) {
        employeeService.deleteEmployees(ids);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/department/{deptId}")
    public ResponseEntity<List<EmployeeResponseDTO>> getEmployeesByDepartmentId(@PathVariable Integer deptId) {
        List<EmployeeResponseDTO> responseDTOs = employeeService.getEmployeesByDepartmentId(deptId);
        return ResponseEntity.ok(responseDTOs);
    }
}
