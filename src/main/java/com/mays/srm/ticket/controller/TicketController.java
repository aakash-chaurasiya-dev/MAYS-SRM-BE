package com.mays.srm.ticket.controller;

import com.mays.srm.ticket.entities.Ticket;
import com.mays.srm.ticket.entities.TicketAttachment;
import com.mays.srm.ticket.dto.resDTO.TicketDashboardTicketStatsResponseDTO;
import com.mays.srm.ticket.dto.request.TicketRequestDTO;
import com.mays.srm.ticket.dto.resDTO.TicketResponseDTO;
import com.mays.srm.ticket.dto.resDTO.TicketUserDashboardResponseDTO;
import com.mays.srm.ticket.dto.resDTO.TicketDashboardResponseDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.mays.srm.ticket.service.TicketAttachmentService;
import com.mays.srm.ticket.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private TicketAttachmentService ticketAttachmentService;

    @PostMapping
    public ResponseEntity<TicketResponseDTO> createTicket(@RequestBody TicketRequestDTO requestDTO) {
        TicketResponseDTO responseDTO = ticketService.create(requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponseDTO> getTicketById(@PathVariable Integer id) {
        TicketResponseDTO responseDTO = ticketService.getById(id);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<TicketResponseDTO>> getAllTickets() {
        List<TicketResponseDTO> responseDTOs = ticketService.getAll();
        return ResponseEntity.ok(responseDTOs);
    }

    // --- DashBoard Endpoints ---
    @GetMapping("/dashboard")
    public ResponseEntity<Page<TicketDashboardResponseDTO>> getTickets(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit) {

        Pageable pageable = PageRequest.of(offset, limit);
        Page<TicketDashboardResponseDTO> tickets = ticketService.getTicketsForDashboard(pageable);

        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/dashboard/stats")
    public ResponseEntity<TicketDashboardTicketStatsResponseDTO> getDashboardStats() {
        return ResponseEntity.ok(ticketService.getDashboardTicketStats());
    }

    @GetMapping("/dashboard/department/{departmentName}")
    public ResponseEntity<Page<TicketDashboardResponseDTO>> getTicketsByDepartment(
            @PathVariable String departmentName,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit) {

        Pageable pageable = PageRequest.of(offset, limit);
        Page<TicketDashboardResponseDTO> tickets = ticketService.getTicketsByDepartmentDashboard(departmentName,
                pageable);

        return ResponseEntity.ok(tickets);
    }

    // --- Update and Delete Endpoints ---
    @PatchMapping("/{id}")
    public ResponseEntity<TicketResponseDTO> updateTicket(@PathVariable Integer id,
            @RequestBody TicketRequestDTO requestDTO) {
        TicketResponseDTO updatedDto = ticketService.update(id, requestDTO);
        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Integer id) {
        ticketService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // --- Search Endpoints ---

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TicketResponseDTO>> getAllTicketsOfUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(ticketService.getAllTicketsOfUser(userId));
    }

    @GetMapping("/user/dashboard/{userId}")
    public ResponseEntity<List<TicketUserDashboardResponseDTO>> getLightweightTicketsByUserId(@PathVariable Integer userId) {
        return ResponseEntity.ok(ticketService.getLightweightTicketsByUserId(userId));
    }

    @GetMapping("/branch/{branchId}")
    public ResponseEntity<List<TicketResponseDTO>> getAllTicketsOfBranch(@PathVariable int branchId) {
        return ResponseEntity.ok(ticketService.getAllTicketsOfBranch(branchId));
    }

    @GetMapping("/status/{statusId}")
    public ResponseEntity<List<TicketResponseDTO>> getAllTicketsOfStatus(@PathVariable int statusId) {
        return ResponseEntity.ok(ticketService.getAllTicketsOfStatus(statusId));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<TicketResponseDTO>> getAllTicketsOfEmployee(@PathVariable int employeeId) {
        return ResponseEntity.ok(ticketService.getAllTicketsOfEmployee(employeeId));
    }

    // --- Attachment Endpoints ---

    @PostMapping("/{ticketId}/attachments")
    public ResponseEntity<TicketAttachment> uploadAttachment(
            @PathVariable int ticketId,
            @RequestParam("file") MultipartFile file) throws Exception {
        TicketAttachment savedAttachment = ticketAttachmentService.uploadAttachment(ticketId, file);
        return ResponseEntity.ok(savedAttachment);
    }

    @GetMapping("/{ticketId}/attachments")
    public ResponseEntity<List<TicketAttachment>> getTicketAttachments(@PathVariable int ticketId) {
        List<TicketAttachment> attachments = ticketAttachmentService.getTicketAttachments(ticketId);
        return ResponseEntity.ok(attachments);
    }

    @DeleteMapping("/{ticketId}/attachments/{attachmentId}")
    public ResponseEntity<Void> deleteAttachment(@PathVariable int ticketId, @PathVariable Long attachmentId) {
        try {
            ticketAttachmentService.deleteAttachment(ticketId, attachmentId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().build();
        }
    }
}
