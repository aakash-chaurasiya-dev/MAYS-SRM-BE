package com.mays.srm.ticket.controller;
import com.mays.srm.dao.core.TicketAttachmentDao;
import com.mays.srm.ticket.entities.Ticket;
import com.mays.srm.ticket.dto.resDTO.DashboardTicketStatsResponseDTO;
import com.mays.srm.ticket.dto.request.TicketRequestDTO;
import com.mays.srm.ticket.dto.resDTO.TicketResponseDTO;
import com.mays.srm.ticket.dto.resDTO.TicketDashboardResponseDTO;
import com.mays.srm.entity.TicketAttachment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.mays.srm.service.FileServerService;
import com.mays.srm.ticket.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private FileServerService fileServerService;

    // Assuming you will create a service for attachments later
    // For now, direct DAO usage is here for simplicity
    @Autowired
    private com.mays.srm.dao.core.TicketAttachmentDao ticketAttachmentDao;

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
    public ResponseEntity<DashboardTicketStatsResponseDTO> getDashboardStats() {
        return ResponseEntity.ok(ticketService.getDashboardTicketStats());
    }

    @GetMapping("/dashboard/department/{departmentName}")
    public ResponseEntity<Page<TicketDashboardResponseDTO>> getTicketsByDepartment(
            @PathVariable String departmentName,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit) {
        
        Pageable pageable = PageRequest.of(offset, limit);
        Page<TicketDashboardResponseDTO> tickets = ticketService.getTicketsByDepartmentDashboard(departmentName, pageable);
        
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

        String sanitizedFilename = "unknown_file";
        if (file.getOriginalFilename() != null) {
            sanitizedFilename = file.getOriginalFilename().replaceAll("\\s+", "_");
        }

        String fileUrl = fileServerService.uploadFile(file, sanitizedFilename);

        // This part should ideally be in an AttachmentService
        TicketResponseDTO ticketDto = ticketService.getById(ticketId); // Ensures ticket exists
        Ticket ticket = new Ticket();
        ticket.setTicketId(ticketDto.getTicketId());

        TicketAttachment attachment = new TicketAttachment();
        attachment.setTicket(ticket);
        attachment.setFileUrl(fileUrl);
        attachment.setFileName(sanitizedFilename);

        TicketAttachment savedAttachment = ticketAttachmentDao.save(attachment);

        return ResponseEntity.ok(savedAttachment);
    }

    @GetMapping("/{ticketId}/attachments")
    public ResponseEntity<List<TicketAttachment>> getTicketAttachments(@PathVariable int ticketId) {
        ticketService.getById(ticketId); // Ensures ticket exists
        List<TicketAttachment> attachments = ticketAttachmentDao.findAllByTicketTicketId(ticketId);
        return ResponseEntity.ok(attachments);
    }
}
