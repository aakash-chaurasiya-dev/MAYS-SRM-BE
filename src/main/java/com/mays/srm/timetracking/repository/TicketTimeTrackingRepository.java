package com.mays.srm.timetracking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mays.srm.timetracking.entities.TicketTimeTracking;

@Repository
public interface TicketTimeTrackingRepository extends JpaRepository<TicketTimeTracking, Long> {

    List<TicketTimeTracking> findByTicketTicketIdAndIsActiveTrue(Integer ticketId);

    List<TicketTimeTracking> findByTicketTicketId(Integer ticketId);

    List<TicketTimeTracking> findByAssigneeEmployeeId(Integer employeeId);
}
