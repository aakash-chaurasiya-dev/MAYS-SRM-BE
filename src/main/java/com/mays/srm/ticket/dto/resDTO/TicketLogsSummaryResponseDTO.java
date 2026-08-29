package com.mays.srm.ticket.dto.resDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketLogsSummaryResponseDTO {
    /** Modified by (employee who made the change) */
    private String modifiedBy;
    /** Status after the change */
    private String status;
    /** Date of the activity */
    private LocalDateTime modificationDate;
    /** Remark / assignor remarks */
    private String assignorRemarks;
    /** Assigned (assignor) */
    private String assignorEmployeeName;
    /** Assigned to (assignee) */
    private String assigneeEmployeeName;
}
