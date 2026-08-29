package com.mays.srm.inventory.dto.request;

import lombok.Data;

@Data
public class PartsOrderOpenRequestDTO {
    private Integer ticketPartId;
    private Integer ticketId;
    private Integer partCatId;
    private Integer quantity;
}
