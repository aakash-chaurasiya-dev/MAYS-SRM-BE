package com.mays.srm.dto.requestDTO;

import lombok.Data;

@Data
public class PartsRequestDTO {
    private Integer ticketId;
    private String partName;
    private Integer quantity;
    private Integer deviceTypeId;
    private Integer statusId;
    private Boolean returned;
}
