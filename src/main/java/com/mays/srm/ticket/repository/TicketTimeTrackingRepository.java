package com.mays.srm.ticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mays.srm.ticket.entities.TicketTimeTracking;
import java.util.List;

@Repository
public interface TicketTimeTrackingRepository extends JpaRepository<TicketTimeTracking, Long> {
    
    // Find active tracking records for a specific ticket
    List<TicketTimeTracking> findByTicketTicketIdAndIsActiveTrue(Integer ticketId);
    
    // Find all tracking records for a specific ticket
    List<TicketTimeTracking> findByTicketTicketId(Integer ticketId);
    
    // Find all tracking records for an assignee
    List<TicketTimeTracking> findByAssigneeEmployeeId(Integer employeeId);
}
