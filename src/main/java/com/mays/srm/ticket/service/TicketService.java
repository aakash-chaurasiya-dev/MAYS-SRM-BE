package com.mays.srm.ticket.service;
import com.mays.srm.ticket.dto.resDTO.DashboardTicketStatsResponseDTO;
import com.mays.srm.service.GenericService;
import com.mays.srm.ticket.dto.request.TicketRequestDTO;
import com.mays.srm.ticket.dto.resDTO.TicketResponseDTO;
import com.mays.srm.ticket.dto.resDTO.TicketDashboardResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TicketService extends GenericService<TicketRequestDTO, TicketResponseDTO, Integer> {
    List<TicketResponseDTO> getAllTicketsOfUser(Integer userId);
    List<TicketResponseDTO> getAllTicketsOfBranch(int branchId);
    List<TicketResponseDTO> getAllTicketsOfStatus(int statusId);
    List<TicketResponseDTO> getAllTicketsOfEmployee(int employeeId);
    Page<TicketDashboardResponseDTO> getTicketsForDashboard(Pageable pageable);
    DashboardTicketStatsResponseDTO getDashboardTicketStats();
    Page<TicketDashboardResponseDTO> getTicketsByDepartmentDashboard(String departmentName, Pageable pageable);
}
