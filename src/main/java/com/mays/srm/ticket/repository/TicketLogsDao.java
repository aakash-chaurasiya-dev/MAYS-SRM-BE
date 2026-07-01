package com.mays.srm.ticket.repository;
import com.mays.srm.ticket.repository.custom.TicketLogsDaoCustom;
import com.mays.srm.ticket.entities.TicketLogs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TicketLogsDao extends JpaRepository<TicketLogs, Integer>, TicketLogsDaoCustom {
   
}

