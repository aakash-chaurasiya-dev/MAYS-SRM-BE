package com.mays.srm.dao.core;

import com.mays.srm.entity.TicketLogs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketLogsDao extends JpaRepository<TicketLogs, Integer> {
    List<TicketLogs> findByTicketTicketIdOrderByModificationDateDesc(Integer ticketId);
}
