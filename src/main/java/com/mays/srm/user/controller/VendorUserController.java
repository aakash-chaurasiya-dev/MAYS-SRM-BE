package com.mays.srm.user.controller;

import com.mays.srm.user.dto.reqDTO.VendorUserRequestDTO;
import com.mays.srm.user.dto.resDTO.VendorUserResponseDTO;
import com.mays.srm.user.service.VendorUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendor-users")
public class VendorUserController {

    @Autowired
    private VendorUserService vendorUserService;

    @PostMapping
    public ResponseEntity<VendorUserResponseDTO> createVendorUser(@RequestBody VendorUserRequestDTO requestDTO) {
        VendorUserResponseDTO responseDTO = vendorUserService.create(requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VendorUserResponseDTO> getVendorUserById(@PathVariable Integer id) {
        VendorUserResponseDTO responseDTO = vendorUserService.getById(id);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public ResponseEntity<Page<VendorUserResponseDTO>> getAllVendorUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<VendorUserResponseDTO> responseDTOs = vendorUserService.getAll(pageable);
        return ResponseEntity.ok(responseDTOs);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VendorUserResponseDTO> updateVendorUser(@PathVariable Integer id, @RequestBody VendorUserRequestDTO requestDTO) {
        VendorUserResponseDTO updatedDto = vendorUserService.update(id, requestDTO);
        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVendorUser(@PathVariable Integer id) {
        vendorUserService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<List<VendorUserResponseDTO>> getVendorUsersByVendorId(@PathVariable Integer vendorId) {
        List<VendorUserResponseDTO> responseDTOs = vendorUserService.getByVendorId(vendorId);
        return ResponseEntity.ok(responseDTOs);
    }
}
