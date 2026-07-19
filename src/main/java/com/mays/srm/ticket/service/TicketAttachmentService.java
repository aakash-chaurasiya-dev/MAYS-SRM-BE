package com.mays.srm.ticket.service;

import com.mays.srm.ticket.entities.TicketAttachment;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface TicketAttachmentService {
    TicketAttachment uploadAttachment(int ticketId, MultipartFile file) throws Exception;
    List<TicketAttachment> getTicketAttachments(int ticketId);
    void deleteAttachment(int ticketId, Long attachmentId);
}
