package com.mays.srm.ticket.service.impl;

import com.mays.srm.ticket.entities.Ticket;
import com.mays.srm.ticket.entities.TicketAttachment;
import com.mays.srm.ticket.repository.TicketAttachmentDao;
import com.mays.srm.ticket.service.FileServerService;
import com.mays.srm.ticket.service.TicketAttachmentService;
import com.mays.srm.ticket.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class TicketAttachmentServiceImpl implements TicketAttachmentService {

    @Autowired
    private TicketAttachmentDao ticketAttachmentDao;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private FileServerService fileServerService;

    @Override
    @Transactional
    public TicketAttachment uploadAttachment(int ticketId, MultipartFile file) throws Exception {
        // Ensure ticket exists
        ticketService.getById(ticketId);

        String originalFilename = file.getOriginalFilename();
        String sanitizedFilename = "unknown_file";
        if (originalFilename != null) {
            sanitizedFilename = originalFilename.replaceAll("\\s+", "_");
        }

        // Upload to File Server
        String fileUrl = fileServerService.uploadFile(file, sanitizedFilename);

        Ticket ticket = new Ticket();
        ticket.setTicketId(ticketId);

        TicketAttachment attachment = new TicketAttachment();
        attachment.setTicket(ticket);
        attachment.setFileUrl(fileUrl);
        attachment.setFileName(sanitizedFilename);

        return ticketAttachmentDao.save(attachment);
    }

    @Override
    public List<TicketAttachment> getTicketAttachments(int ticketId) {
        ticketService.getById(ticketId); // Ensure ticket exists
        return ticketAttachmentDao.findAllByTicketTicketId(ticketId);
    }

    @Override
    @Transactional
    public void deleteAttachment(int ticketId, Long attachmentId) {
        ticketService.getById(ticketId); // Ensure ticket exists
        Optional<TicketAttachment> attachmentOpt = ticketAttachmentDao.findById(attachmentId);
        if (attachmentOpt.isPresent()) {
            TicketAttachment attachment = attachmentOpt.get();
            if (attachment.getTicket().getTicketId() == ticketId) {
                // 1. Delete from database first
                ticketAttachmentDao.delete(attachment);

                // 2. Perform best-effort remote file server deletion
                String fileUrl = attachment.getFileUrl();
                if (fileUrl != null && fileUrl.contains("/")) {
                    String uniqueFilename = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
                    try {
                        fileServerService.deleteFile(uniqueFilename);
                    } catch (Exception e) {
                        System.err.println("Warning: Failed to delete attachment from File Server: " + e.getMessage());
                    }
                }
            } else {
                throw new IllegalArgumentException("Attachment does not belong to this ticket");
            }
        } else {
            throw new IllegalArgumentException("Attachment not found");
        }
    }
}
