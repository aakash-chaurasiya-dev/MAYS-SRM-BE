package com.mays.srm.dao.core;
import com.mays.srm.ticket.entities.TicketType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketTypeDao extends JpaRepository<TicketType, Integer> {
}
