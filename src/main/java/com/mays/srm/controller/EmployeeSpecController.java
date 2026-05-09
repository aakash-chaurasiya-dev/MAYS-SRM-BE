package com.mays.srm.controller;

import com.mays.srm.entity.EmployeeSpec;
import com.mays.srm.entity.EmployeeSpecId;
import com.mays.srm.service.EmployeeSpecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/employee-specs")
public class EmployeeSpecController {

    @Autowired
    private EmployeeSpecService service;

    @PostMapping
    public ResponseEntity<EmployeeSpec> create(@RequestBody EmployeeSpec entity) {
        return ResponseEntity.ok(service.create(entity));
    }

    @GetMapping
    public ResponseEntity<List<EmployeeSpec>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{employeeId}/{deviceId}")
    public ResponseEntity<EmployeeSpec> getById(@PathVariable Integer employeeId, @PathVariable Integer deviceId) {
        EmployeeSpecId id = new EmployeeSpecId(employeeId, deviceId);
        Optional<EmployeeSpec> entity = service.getById(id);
        return entity.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{employeeId}/{deviceId}")
    public ResponseEntity<Void> delete(@PathVariable Integer employeeId, @PathVariable Integer deviceId) {
        EmployeeSpecId id = new EmployeeSpecId(employeeId, deviceId);
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}
