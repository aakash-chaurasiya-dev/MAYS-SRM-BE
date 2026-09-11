package com.mays.srm.enquiry.controller;

import com.mays.srm.enquiry.dto.reqDTO.EnquiryMarkActionRequestDTO;
import com.mays.srm.enquiry.dto.reqDTO.EnquiryRequestDTO;
import com.mays.srm.enquiry.dto.resDTO.EnquiryPendingCountDTO;
import com.mays.srm.enquiry.dto.resDTO.EnquiryResponseDTO;
import com.mays.srm.enquiry.service.EnquiryService;
import com.mays.srm.security.core.CustomUserDetails;
import com.mays.srm.security.util.SecurityUtils;
import com.mays.srm.ticket.dto.resDTO.TicketResponseDTO;
import com.mays.srm.timetracking.util.StatusAccessValidator;
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
        return ResponseEntity.ok(enquiryService.create(requestDTO));
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
        return ResponseEntity.ok(enquiryService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<EnquiryResponseDTO>> getAllEnquiries() {
        return ResponseEntity.ok(enquiryService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<EnquiryResponseDTO> updateEnquiry(@PathVariable Integer id,
                                                            @RequestBody EnquiryRequestDTO requestDTO) {
        return ResponseEntity.ok(enquiryService.update(id, requestDTO));
    }

    // ── NEW: fetch enquiry linked to a ticket ──
    @GetMapping("/by-ticket/{ticketId}")
    public ResponseEntity<EnquiryResponseDTO> getByTicketId(@PathVariable Integer ticketId) {
        EnquiryResponseDTO dto = enquiryService.getByTicketId(ticketId);
        return dto == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(dto);
    }

    @PostMapping("/by-ticket/{ticketId}/mark-outward")
    public ResponseEntity<Void> markOutwardByTicket(@PathVariable Integer ticketId) {
        enquiryService.markOutwardByTicket(ticketId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/convert-to-ticket")
    public ResponseEntity<TicketResponseDTO> convertToTicket(
            @PathVariable Integer id,
            @RequestParam(required = false) Integer employeeId) {
        Integer resolvedEmployeeId = employeeId;
        if (resolvedEmployeeId == null) {
            resolvedEmployeeId = StatusAccessValidator.getCurrentEmployeeId();
        }
        return ResponseEntity.ok(enquiryService.convertToTicket(id, resolvedEmployeeId));
    }

    @PostMapping("/{id}/mark-action")
    public ResponseEntity<Void> markAction(@PathVariable Integer id,
                                           @RequestBody EnquiryMarkActionRequestDTO requestDTO) {
        enquiryService.markAction(id, requestDTO);
        return ResponseEntity.ok().build();
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