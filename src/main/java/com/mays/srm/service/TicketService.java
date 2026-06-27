package com.mays.srm.service;

import com.mays.srm.dto.requestDTO.TicketDTO.TicketRequestDTO;
import com.mays.srm.dto.responseDTO.TicketDTO.TicketResponseDTO;
import com.mays.srm.dto.responseDTO.TicketDTO.TicketDashboardResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TicketService extends GenericService<TicketRequestDTO, TicketResponseDTO, Integer> {
    List<TicketResponseDTO> getAllTicketsOfUser(Integer userId);
    List<TicketResponseDTO> getAllTicketsOfBranch(int branchId);
    List<TicketResponseDTO> getAllTicketsOfStatus(int statusId);
    List<TicketResponseDTO> getAllTicketsOfEmployee(int employeeId);
    Page<TicketDashboardResponseDTO> getTicketsForDashboard(Pageable pageable);
    com.mays.srm.dto.responseDTO.TicketDTO.DashboardTicketStatsResponseDTO getDashboardTicketStats();
    Page<TicketDashboardResponseDTO> getTicketsByDepartmentDashboard(String departmentName, Pageable pageable);
}
