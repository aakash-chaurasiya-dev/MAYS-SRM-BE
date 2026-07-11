package com.mays.srm.ticket.service;

import java.util.List;
import com.mays.srm.ticket.dto.resDTO.TicketTimeTrackingResponseDTO;
import com.mays.srm.ticket.dto.resDTO.EmployeeTicketStatsDTO;
import com.mays.srm.ticket.dto.resDTO.EmployeeTicketHistoryDTO;
import com.mays.srm.ticket.entities.Ticket;
import com.mays.srm.organization.entities.Status;
import com.mays.srm.user.entities.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TicketTimeTrackingService {
    List<TicketTimeTrackingResponseDTO> getTimeTrackingByTicketId(Integer ticketId);
    TicketTimeTrackingResponseDTO getTrackingById(Long id);
    
    void startTrackingForNewTicket(Ticket ticket);
    void handleTrackingUpdates(Ticket updatedTicket, Status oldStatus, Employee oldAssignee);
    
    EmployeeTicketStatsDTO getEmployeeTicketStats(Integer employeeId);
    Page<EmployeeTicketHistoryDTO> getEmployeeTicketHistory(Integer employeeId, Pageable pageable);
}
