package com.mays.srm.ticket.repository.custom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.mays.srm.ticket.dto.resDTO.TicketDashboardResponseDTO;

import com.mays.srm.ticket.dto.resDTO.TicketDashboardTicketStatsResponseDTO;

public interface TicketDaoCustom {
    Page<TicketDashboardResponseDTO> getAllTicketDashboard(Pageable pageable);
    
    TicketDashboardTicketStatsResponseDTO getDashboardTicketStats();
    
    Page<TicketDashboardResponseDTO> getTicketsByDepartmentDashboard(String departmentName, Pageable pageable);
}
