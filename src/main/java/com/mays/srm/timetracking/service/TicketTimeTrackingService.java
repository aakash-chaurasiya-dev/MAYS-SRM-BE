package com.mays.srm.timetracking.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mays.srm.ticket.entities.Ticket;
import com.mays.srm.organization.entities.Status;
import com.mays.srm.user.entities.Employee;
import com.mays.srm.timetracking.dto.resDTO.EmployeeTicketHistoryDTO;
import com.mays.srm.timetracking.dto.resDTO.EmployeeTicketStatsDTO;
import com.mays.srm.timetracking.dto.resDTO.TicketTimeTrackingResponseDTO;

public interface TicketTimeTrackingService {

    List<TicketTimeTrackingResponseDTO> getTimeTrackingByTicketId(Integer ticketId);

    TicketTimeTrackingResponseDTO getTrackingById(Long id);

    void startTrackingForNewTicket(Ticket ticket);

    void handleTrackingUpdates(Ticket updatedTicket, Status oldStatus, Employee oldAssignee, String holdReason);

    EmployeeTicketStatsDTO getEmployeeTicketStats(Integer employeeId);

    Page<EmployeeTicketHistoryDTO> getEmployeeTicketHistory(Integer employeeId, Pageable pageable);
}
