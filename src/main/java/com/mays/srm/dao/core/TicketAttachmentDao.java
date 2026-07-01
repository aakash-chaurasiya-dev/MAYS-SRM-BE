package com.mays.srm.dao.core;
import com.mays.srm.entity.TicketAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketAttachmentDao extends JpaRepository<TicketAttachment, Long> {
    /**
     * Finds all attachments associated with a specific ticket ID.
     * Spring Data JPA will automatically generate the query based on the method name.
     * @param ticketId The ID of the ticket.
     * @return A list of attachments for the given ticket.
     */
    List<TicketAttachment> findAllByTicketTicketId(Integer ticketId);
}
