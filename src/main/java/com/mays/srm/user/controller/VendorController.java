package com.mays.srm.user.controller;

import com.mays.srm.user.dto.reqDTO.VendorRequestDTO;
import com.mays.srm.user.dto.resDTO.VendorResponseDTO;
import com.mays.srm.user.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendors")
public class VendorController {

    @Autowired
    private VendorService vendorService;

    @PostMapping
    public ResponseEntity<VendorResponseDTO> createVendor(@RequestBody VendorRequestDTO requestDTO) {
        VendorResponseDTO responseDTO = vendorService.create(requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VendorResponseDTO> getVendorById(@PathVariable Integer id) {
        VendorResponseDTO responseDTO = vendorService.getById(id);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<VendorResponseDTO>> getAllVendors() {
        List<VendorResponseDTO> responseDTOs = vendorService.getAll();
        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<VendorResponseDTO>> getPaginatedVendors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<VendorResponseDTO> responseDTOs = vendorService.getPaginated(pageable);
        return ResponseEntity.ok(responseDTOs);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VendorResponseDTO> updateVendor(@PathVariable Integer id, @RequestBody VendorRequestDTO requestDTO) {
        VendorResponseDTO updatedDto = vendorService.update(id, requestDTO);
        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVendor(@PathVariable Integer id) {
        vendorService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/mobile")
    public ResponseEntity<VendorResponseDTO> findByMobileNo(@RequestParam String mobileNo) {
        VendorResponseDTO responseDTO = vendorService.findByMobileNo(mobileNo);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/email")
    public ResponseEntity<VendorResponseDTO> findByEmail(@RequestParam String email) {
        VendorResponseDTO responseDTO = vendorService.findByEmail(email);
        return ResponseEntity.ok(responseDTO);
    }
}
