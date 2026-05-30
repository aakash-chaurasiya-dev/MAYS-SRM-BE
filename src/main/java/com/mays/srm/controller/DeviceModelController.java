package com.mays.srm.controller;

import com.mays.srm.dto.requestDTO.DeviceModelRequestDTO;
import com.mays.srm.dto.responseDTO.DeviceModelResponseDTO;
import com.mays.srm.service.DeviceModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devicemodels")
public class DeviceModelController {

    @Autowired
    private DeviceModelService deviceModelService;

    @PostMapping
    public ResponseEntity<DeviceModelResponseDTO> createDeviceModel(@RequestBody DeviceModelRequestDTO requestDTO) {
        DeviceModelResponseDTO responseDTO = deviceModelService.create(requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceModelResponseDTO> getDeviceModelById(@PathVariable Integer id) {
        DeviceModelResponseDTO responseDTO = deviceModelService.getById(id);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<DeviceModelResponseDTO>> getAllDeviceModels() {
        List<DeviceModelResponseDTO> responseDTOs = deviceModelService.getAll();
        return ResponseEntity.ok(responseDTOs);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeviceModelResponseDTO> updateDeviceModel(@PathVariable Integer id, @RequestBody DeviceModelRequestDTO requestDTO) {
        DeviceModelResponseDTO updatedDto = deviceModelService.update(id, requestDTO);
        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeviceModel(@PathVariable Integer id) {
        deviceModelService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
