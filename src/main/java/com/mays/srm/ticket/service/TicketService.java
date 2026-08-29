package com.mays.srm.ticket.service;
import com.mays.srm.ticket.dto.resDTO.TicketDashboardTicketStatsResponseDTO;
import com.mays.srm.core.service.GenericService;
import com.mays.srm.ticket.dto.request.TicketRequestDTO;
import com.mays.srm.ticket.dto.resDTO.TicketResponseDTO;
import com.mays.srm.ticket.dto.resDTO.TicketDashboardResponseDTO;
import com.mays.srm.ticket.dto.resDTO.TicketUserDashboardResponseDTO;
import com.mays.srm.user.entities.UserMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TicketService extends GenericService<TicketRequestDTO, TicketResponseDTO, Integer> {
    List<TicketResponseDTO> getAllTicketsOfUser(Integer userId);
    List<TicketResponseDTO> getAllTicketsOfBranch(int branchId);
    List<TicketResponseDTO> getAllTicketsOfStatus(int statusId);
    List<TicketResponseDTO> getAllTicketsOfEmployee(int employeeId);
    List<TicketResponseDTO> getAllTicketsOfVendor(Integer vendorId);
    Page<TicketDashboardResponseDTO> getTicketsForDashboard(Pageable pageable);
    TicketDashboardTicketStatsResponseDTO getDashboardTicketStats();
    Page<TicketDashboardResponseDTO> getTicketsByDepartmentDashboard(String departmentName, Pageable pageable);
    List<TicketUserDashboardResponseDTO> getLightweightTicketsByUserId(Integer userId);
}
