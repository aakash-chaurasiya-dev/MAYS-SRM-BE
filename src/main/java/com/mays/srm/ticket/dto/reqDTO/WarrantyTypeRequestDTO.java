package com.mays.srm.ticket.dto.request;

import lombok.Data;

@Data
public class WarrantyTypeRequestDTO {
    private String warrantyTypeName;
    private Integer ticketTypeId;
    private String warrantyTypeDescription;
    private Boolean isLocked;
}
