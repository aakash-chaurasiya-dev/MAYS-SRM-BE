package com.mays.srm.dto.responseDTO.TicketLogsDTO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TicketLogsResponseDTO {
    private Integer logId;
    private Integer ticketId;
    private String assignorEmployeeName;
    private String assigneeEmployeeName;
    private LocalDateTime modificationDate;
    private String oldStatus;
    private String newStatus;
    private String changedFields;
    private String modifiedBy;
    private String assignorRemarks;
}
