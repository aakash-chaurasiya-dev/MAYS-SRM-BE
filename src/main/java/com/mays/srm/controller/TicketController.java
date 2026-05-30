package com.mays.srm.controller;

import com.mays.srm.dto.requestDTO.TicketRequestDTO;
import com.mays.srm.dto.responseDTO.TicketResponseDTO;
import com.mays.srm.entity.TicketAttachment;
import com.mays.srm.service.FileServerService;
import com.mays.srm.service.TicketService;
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

    @PutMapping("/{id}")
    public ResponseEntity<TicketResponseDTO> updateTicket(@PathVariable Integer id, @RequestBody TicketRequestDTO requestDTO) {
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
            @RequestParam("file") MultipartFile file
    ) throws Exception {
        
        String sanitizedFilename = "unknown_file";
        if (file.getOriginalFilename() != null) {
            sanitizedFilename = file.getOriginalFilename().replaceAll("\\s+", "_");
        }

        String fileUrl = fileServerService.uploadFile(file, sanitizedFilename);

        // This part should ideally be in an AttachmentService
        TicketResponseDTO ticketDto = ticketService.getById(ticketId); // Ensures ticket exists
        com.mays.srm.entity.Ticket ticket = new com.mays.srm.entity.Ticket();
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
