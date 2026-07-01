package com.mays.srm.ticket.dto.resDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketLogsSummaryResponseDTO {
    private String modifiedBy;
    private String status;
    private LocalDateTime modificationDate;
    private String assignorRemarks;
    private String assignorEmployeeName;
    private String assigneeEmployeeName;
}
