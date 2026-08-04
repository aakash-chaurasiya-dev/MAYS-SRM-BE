package com.mays.srm.inventory.repository;
import com.mays.srm.inventory.entities.Parts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartsDao extends JpaRepository<Parts, Integer> {
    List<Parts> findByTicket_TicketId(Integer ticketId);
}
