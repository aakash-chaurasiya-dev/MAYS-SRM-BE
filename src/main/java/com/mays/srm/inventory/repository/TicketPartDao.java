package com.mays.srm.inventory.repository;

import com.mays.srm.inventory.entities.TicketPart;
import com.mays.srm.inventory.repository.custom.TicketPartDaoCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketPartDao extends JpaRepository<TicketPart, Integer>, TicketPartDaoCustom {
}
