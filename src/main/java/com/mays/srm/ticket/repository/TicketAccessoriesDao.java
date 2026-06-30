package com.mays.srm.ticket.repository;

import com.mays.srm.ticket.entities.TicketAccessories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketAccessoriesDao extends JpaRepository<TicketAccessories, Integer> {
    List<TicketAccessories> findByTicket_TicketId(Integer ticketId);
}
