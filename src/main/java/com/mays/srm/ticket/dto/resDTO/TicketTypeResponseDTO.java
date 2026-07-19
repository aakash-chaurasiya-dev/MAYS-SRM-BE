package com.mays.srm.ticket.dto.resDTO;
import lombok.Data;

@Data
public class TicketTypeResponseDTO {
    private Integer ticketTypeId;
    private String ticketTypeName;
    private String ticketTypeDescription;

    private Boolean isLocked;
    private java.util.Date insertDate;
    private java.util.Date lastUpdateDate;
}
