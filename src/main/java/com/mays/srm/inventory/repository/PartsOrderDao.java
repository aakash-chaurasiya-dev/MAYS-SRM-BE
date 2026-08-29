package com.mays.srm.inventory.repository;

import com.mays.srm.inventory.entities.PartsOrder;
import com.mays.srm.inventory.repository.custom.PartsOrderDaoCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PartsOrderDao extends JpaRepository<PartsOrder, Integer>, PartsOrderDaoCustom {

    Optional<PartsOrder> findByTicketPart_TicketPartId(Integer ticketPartId);
}
