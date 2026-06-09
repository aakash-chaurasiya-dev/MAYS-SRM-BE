package com.mays.srm.dto.requestDTO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PartsRequestDTO {
    private Integer ticketId;
    private String partName;
    private Integer quantity;
    private Integer productId; // Replaced deviceTypeId
    private Integer statusId;
    private Boolean returned;
    private LocalDateTime receiveDate; // Added
    private String remarks; // Added

}
