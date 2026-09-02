package com.mays.srm.enquiry.controller;
import com.mays.srm.enquiry.dto.request.EnquiryRequestDTO;
import com.mays.srm.enquiry.dto.resDTO.EnquiryPendingCountDTO;
import com.mays.srm.enquiry.dto.resDTO.EnquiryResponseDTO;
import com.mays.srm.enquiry.service.EnquiryService;
import com.mays.srm.security.core.CustomUserDetails;
import com.mays.srm.security.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

    @GetMapping("/pending/count")
    public ResponseEntity<EnquiryPendingCountDTO> getPendingCount() {
        Optional<CustomUserDetails> currentUser = SecurityUtils.getCurrentUser();
        if (currentUser.isPresent() && isPortalUser(currentUser.get())) {
            return ResponseEntity.ok(enquiryService.getPendingCountForUser(currentUser.get().getUserId()));
        }
        return ResponseEntity.ok(enquiryService.getPendingCountAll());
    }

    private boolean isPortalUser(CustomUserDetails user) {
        String role = user.getAuthorities().iterator().next().getAuthority();
        return "ROLE_USER".equals(role) || "ROLE_VENDOR".equals(role);
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

    @PostMapping("/{id}/convert-to-ticket")
    public ResponseEntity<com.mays.srm.ticket.dto.resDTO.TicketResponseDTO> convertToTicket(
            @PathVariable Integer id,
            @RequestParam(required = false) Integer employeeId) {
        Integer resolvedEmployeeId = employeeId;
        if (resolvedEmployeeId == null) {
            resolvedEmployeeId = com.mays.srm.timetracking.util.StatusAccessValidator.getCurrentEmployeeId();
        }
        com.mays.srm.ticket.dto.resDTO.TicketResponseDTO ticketResponse = enquiryService.convertToTicket(id, resolvedEmployeeId);
        return ResponseEntity.ok(ticketResponse);
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
