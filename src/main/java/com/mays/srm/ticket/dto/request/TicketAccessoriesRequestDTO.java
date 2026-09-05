package com.mays.srm.ticket.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TicketAccessoriesRequestDTO {
    
    private Integer ticketAccessoriesId;

    @NotNull(message = "Ticket ID is required")
    private Integer ticketId;

    @NotNull(message = "Accessory ID is required")
    private Integer accessoryId;
}
