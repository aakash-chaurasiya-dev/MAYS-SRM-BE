package com.mays.srm.ticket.dto.resDTO;

import lombok.Data;
import java.util.Date;

@Data
public class TicketAccessoriesResponseDTO {
    private Integer ticketAccessoriesId;
    private Integer ticketId;
    private Integer accessoryId;
    private String accessoryName;
    private Date insertDate;
}
