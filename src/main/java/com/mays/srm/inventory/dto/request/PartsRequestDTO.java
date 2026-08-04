package com.mays.srm.inventory.dto.request;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PartsRequestDTO {
    private Integer ticketId;
    private String partName;
    private Integer quantity;
    private Integer productId;
    private Integer statusId;
    /** VENDOR | MARKET | STOCK_IN | STOCK_OUT */
    private String source;
    private Integer vendorId;
    private BigDecimal unitCost;
    private Boolean defectiveReturned;
    /** @deprecated use defectiveReturned */
    private Boolean returned;
    private Boolean customerApproved;
    private LocalDateTime receiveDate;
    private LocalDateTime usedDate;
    private LocalDateTime returnDate;
    private String remarks;
}
