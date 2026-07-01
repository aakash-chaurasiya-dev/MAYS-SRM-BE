package com.mays.srm.ticket.dto.resDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketDashboardTicketStatsResponseDTO {
    private Long totalTickets;
    private List<TicketDashboardDepartmentTicketCountDTO> departmentCounts;
}
