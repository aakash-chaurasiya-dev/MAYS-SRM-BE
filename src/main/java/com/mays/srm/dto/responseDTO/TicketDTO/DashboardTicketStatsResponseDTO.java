package com.mays.srm.dto.responseDTO.TicketDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardTicketStatsResponseDTO {
    private Long totalTickets;
    private List<DepartmentTicketCountDTO> departmentCounts;
}
