package com.mays.srm.controller;

import com.mays.srm.dto.requestDTO.UserMasterRequestDTO;
import com.mays.srm.dto.responseDTO.UserMasterResponseDTO;
import com.mays.srm.service.UserMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserMasterController {

    @Autowired
    private UserMasterService userService;

    @PostMapping
    public ResponseEntity<UserMasterResponseDTO> createUser(@RequestBody UserMasterRequestDTO requestDTO) {
        UserMasterResponseDTO responseDTO = userService.create(requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserMasterResponseDTO> getUserById(@PathVariable Integer id) {
        UserMasterResponseDTO responseDTO = userService.getById(id);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<UserMasterResponseDTO>> getAllUsers() {
        List<UserMasterResponseDTO> responseDTOs = userService.getAll();
        return ResponseEntity.ok(responseDTOs);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserMasterResponseDTO> updateUser(@PathVariable Integer id, @RequestBody UserMasterRequestDTO requestDTO) {
        UserMasterResponseDTO responseDTO = userService.update(id, requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        userService.delete(id); // Use the generic delete from the service implementation
        return ResponseEntity.noContent().build();
    }

    // --- Search Endpoints (Now returning Entities or you could map them to DTOs here too) ---
    // Note: For consistency, it's usually better to map these to DTOs as well, but 
    // I've left them returning entities based on the interface definition, to minimize scope creep.

    @GetMapping("/mobile")
    public ResponseEntity<UserMasterResponseDTO> findByMobileNo(@RequestParam String mobileNo) {
        // Since the interface returns an entity for this, we fetch it and map it manually
        // OR we can change the interface to return DTOs for these as well. Let's return DTO for consistency.
        UserMasterResponseDTO responseDTO = userService.getById(userService.findByMobileNo(mobileNo).getUserId());
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/email")
    public ResponseEntity<UserMasterResponseDTO> findByEmail(@RequestParam String email) {
        UserMasterResponseDTO responseDTO = userService.getById(userService.findByEmail(email).getUserId());
        return ResponseEntity.ok(responseDTO);
    }
}
