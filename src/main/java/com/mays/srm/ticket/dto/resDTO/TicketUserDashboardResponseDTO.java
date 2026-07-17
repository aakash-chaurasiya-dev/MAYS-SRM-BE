package com.mays.srm.ticket.dto.resDTO;

import lombok.Data;

@Data
public class TicketUserDashboardResponseDTO {
    private Integer ticketId;
    private String serialNo;
    private String ticketStatusName;

    // Required by JPA
    public TicketUserDashboardResponseDTO() {
    }

    public TicketUserDashboardResponseDTO(Integer ticketId, String serialNo, String ticketStatusName) {
        this.ticketId = ticketId;
        this.serialNo = serialNo != null ? serialNo : "N/A";
        this.ticketStatusName = ticketStatusName != null ? ticketStatusName : "N/A";
    }
}
