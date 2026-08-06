package com.mays.srm.timetracking.dto.resDTO;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmployeeTicketHistoryDTO {
    private Integer ticketId;
    private String employeeName;
    private String userName;
    private Double hoursSpent;
    private LocalDateTime createdDate;
    private LocalDateTime slaDate;
    private Double targetHours;
    private Boolean isCrossedTAT;
    private String finalRemark;
}
