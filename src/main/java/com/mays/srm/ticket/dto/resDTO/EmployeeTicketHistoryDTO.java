package com.mays.srm.ticket.dto.resDTO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EmployeeTicketHistoryDTO {
    private Integer ticketId;
    private String employeeName;
    private String finalRemark;
    private String userName;
    private Double hoursSpent;
    private LocalDateTime createdDate;
    private LocalDateTime slaDate;
}
