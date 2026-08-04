package com.mays.srm.inventory.dto.resDTO;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PartsResponseDTO {
    private Integer partId;
    private Integer ticketId;
    private String partName;
    private Integer quantity;
    private Integer productId;
    private String productName;
    private Integer statusId;
    private String statusName;
    private String source;
    private Integer vendorId;
    private String vendorName;
    private BigDecimal unitCost;
    private Boolean defectiveReturned;
    private Boolean customerApproved;
    private LocalDateTime orderDate;
    private LocalDateTime receiveDate;
    private LocalDateTime usedDate;
    private LocalDateTime returnDate;
    private String remarks;
    private Boolean stockApplied;
}
