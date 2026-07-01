package com.mays.srm.ticket.dto.resDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketDashboardDepartmentTicketCountDTO {
    private String departmentName;
    private Long ticketCount;
}
