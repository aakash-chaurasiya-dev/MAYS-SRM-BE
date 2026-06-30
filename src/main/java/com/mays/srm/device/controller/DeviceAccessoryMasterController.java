package com.mays.srm.device.controller;

import com.mays.srm.device.dto.request.DeviceAccessoryMasterRequestDTO;
import com.mays.srm.device.dto.resDTO.DeviceAccessoryMasterResponseDTO;
import com.mays.srm.device.service.DeviceAccessoryMasterService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/device-accessories")
public class DeviceAccessoryMasterController {

    private final DeviceAccessoryMasterService service;

    public DeviceAccessoryMasterController(DeviceAccessoryMasterService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<DeviceAccessoryMasterResponseDTO> create(@Valid @RequestBody DeviceAccessoryMasterRequestDTO requestDTO) {
        return new ResponseEntity<>(service.create(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceAccessoryMasterResponseDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<DeviceAccessoryMasterResponseDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/device-type/{deviceTypeId}")
    public ResponseEntity<List<DeviceAccessoryMasterResponseDTO>> getByDeviceTypeId(@PathVariable Integer deviceTypeId) {
        return ResponseEntity.ok(service.getByDeviceTypeId(deviceTypeId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeviceAccessoryMasterResponseDTO> update(@PathVariable Integer id, @Valid @RequestBody DeviceAccessoryMasterRequestDTO requestDTO) {
        return ResponseEntity.ok(service.update(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
