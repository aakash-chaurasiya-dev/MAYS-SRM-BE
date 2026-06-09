package com.mays.srm.dto.responseDTO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PartsResponseDTO {
    private Integer partId;
    private Integer ticketId;
    private String partName;
    private Integer quantity;
    private String productName; // Replaced deviceTypeName
    private String statusName;
    private Boolean returned;
    private LocalDateTime orderDate; // Added
    private LocalDateTime receiveDate; // Added
    private String remarks; // Added
    private Boolean inStock; // Added
}
