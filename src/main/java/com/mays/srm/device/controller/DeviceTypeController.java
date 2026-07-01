package com.mays.srm.device.controller;
import com.mays.srm.device.dto.request.DeviceTypeRequestDTO;
import com.mays.srm.device.dto.resDTO.DeviceTypeResponseDTO;
import com.mays.srm.device.service.DeviceTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devicetypes")
public class DeviceTypeController {

    @Autowired
    private DeviceTypeService deviceTypeService;

    @PostMapping
    public ResponseEntity<DeviceTypeResponseDTO> createDeviceType(@RequestBody DeviceTypeRequestDTO requestDTO) {
        DeviceTypeResponseDTO responseDTO = deviceTypeService.create(requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceTypeResponseDTO> getDeviceTypeById(@PathVariable Integer id) {
        DeviceTypeResponseDTO responseDTO = deviceTypeService.getById(id);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<DeviceTypeResponseDTO>> getAllDeviceTypes() {
        List<DeviceTypeResponseDTO> responseDTOs = deviceTypeService.getAll();
        return ResponseEntity.ok(responseDTOs);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeviceTypeResponseDTO> updateDeviceType(@PathVariable Integer id, @RequestBody DeviceTypeRequestDTO requestDTO) {
        DeviceTypeResponseDTO updatedDto = deviceTypeService.update(id, requestDTO);
        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeviceType(@PathVariable Integer id) {
        deviceTypeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
