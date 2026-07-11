package com.mays.srm.ticket.dto.resDTO;

import lombok.Data;

@Data
public class EmployeeTicketStatsDTO {
    private int totalTickets;
    private int openTickets;
    private int closedTickets;
}
