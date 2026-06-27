package com.mays.srm.dao.custom;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.mays.srm.dto.responseDTO.TicketDTO.TicketDashboardResponseDTO;

import com.mays.srm.dto.responseDTO.TicketDTO.DashboardTicketStatsResponseDTO;

public interface TicketDaoCustom {
    Page<TicketDashboardResponseDTO> getAllTicketDashboard(Pageable pageable);
    
    DashboardTicketStatsResponseDTO getDashboardTicketStats();
    
    Page<TicketDashboardResponseDTO> getTicketsByDepartmentDashboard(String departmentName, Pageable pageable);
}
