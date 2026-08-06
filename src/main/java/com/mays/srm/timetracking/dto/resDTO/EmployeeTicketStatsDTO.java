package com.mays.srm.timetracking.dto.resDTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmployeeTicketStatsDTO {
    private Integer totalTickets;
    private Integer openTickets;
    private Integer closedTickets;
}
