package com.mays.srm.device.controller;
import com.mays.srm.device.dto.resDTO.DeviceResponseDTO;
import com.mays.srm.device.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    /*
     * Commented out because Devices are exclusively created and updated during the Ticket lifecycle.
     * Standalone device manipulation (create/update/delete) is disabled to prevent orphaned data.
     */
    /*
    @PostMapping
    public ResponseEntity<DeviceResponseDTO> createDevice(@RequestBody DeviceRequestDTO requestDTO) {
        DeviceResponseDTO responseDTO = deviceService.create(requestDTO);
        return ResponseEntity.ok(responseDTO);
    }
    */

    @GetMapping("/{id}")
    public ResponseEntity<DeviceResponseDTO> getDeviceById(@PathVariable String id) {
        DeviceResponseDTO responseDTO = deviceService.getById(id);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<DeviceResponseDTO>> getAllDevices() {
        List<DeviceResponseDTO> responseDTOs = deviceService.getAll();
        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<DeviceResponseDTO>> getPaginatedDevices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DeviceResponseDTO> responseDTOs = deviceService.getPaginated(pageable);
        return ResponseEntity.ok(responseDTOs);
    }

    /*
     * Commented out to prevent orphaned data.
     * Updates and deletes should be handled via the Ticket lifecycle.
     */
    /*
    @PutMapping("/{id}")
    public ResponseEntity<DeviceResponseDTO> updateDevice(@PathVariable String id, @RequestBody DeviceRequestDTO requestDTO) {
        DeviceResponseDTO updatedDto = deviceService.update(id, requestDTO);
        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable String id) {
        deviceService.delete(id);
        return ResponseEntity.noContent().build();
    }
    */
}
