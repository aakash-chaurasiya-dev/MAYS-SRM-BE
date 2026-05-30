package com.mays.srm.dto.responseDTO;

import lombok.Data;

@Data
public class PartsResponseDTO {
    private Integer partId;
    private Integer ticketId;
    private String partName;
    private Integer quantity;
    private String deviceTypeName;
    private String statusName;
    private Boolean returned;
}
