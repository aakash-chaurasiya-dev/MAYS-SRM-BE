package com.mays.srm.enquiry.controller;
import com.mays.srm.enquiry.dto.request.EnquiryRequestDTO;
import com.mays.srm.enquiry.dto.resDTO.EnquiryResponseDTO;
import com.mays.srm.enquiry.service.EnquiryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enquiries")
public class EnquiryController {

    @Autowired
    private EnquiryService enquiryService;

    @PostMapping
    public ResponseEntity<EnquiryResponseDTO> createEnquiry(@RequestBody EnquiryRequestDTO requestDTO) {
        EnquiryResponseDTO responseDTO = enquiryService.create(requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnquiryResponseDTO> getEnquiryById(@PathVariable Integer id) {
        EnquiryResponseDTO responseDTO = enquiryService.getById(id);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<EnquiryResponseDTO>> getAllEnquiries() {
        List<EnquiryResponseDTO> responseDTOs = enquiryService.getAll();
        return ResponseEntity.ok(responseDTOs);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EnquiryResponseDTO> updateEnquiry(@PathVariable Integer id, @RequestBody EnquiryRequestDTO requestDTO) {
        EnquiryResponseDTO updatedDto = enquiryService.update(id, requestDTO);
        return ResponseEntity.ok(updatedDto);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<EnquiryResponseDTO>> getAllEnquiriesOfUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(enquiryService.getAllEnquiriesOfUser(userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnquiry(@PathVariable Integer id) {
        enquiryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
