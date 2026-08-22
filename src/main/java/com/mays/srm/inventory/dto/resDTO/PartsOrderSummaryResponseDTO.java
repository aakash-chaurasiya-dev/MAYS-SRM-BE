package com.mays.srm.inventory.dto.resDTO;

import com.mays.srm.inventory.enums.PartsOrderStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class PartsOrderSummaryResponseDTO {

    private Integer orderId;
    private Integer ticketId;
    private Integer partCatId;
    private String partName;
    private Integer quantity;
    private PartsOrderStatus status;
    private BigDecimal totalPrice;
    private Integer orderedBy;
    private String orderedByName;
    private LocalDateTime createdAt;

    public PartsOrderSummaryResponseDTO(
            Integer orderId,
            Integer ticketId,
            Integer partCatId,
            String partName,
            Integer quantity,
            String status,
            BigDecimal totalPrice,
            Integer orderedBy,
            String orderedByName,
            LocalDateTime createdAt) {
        this.orderId = orderId;
        this.ticketId = ticketId;
        this.partCatId = partCatId;
        this.partName = partName;
        this.quantity = quantity;
        this.status = PartsOrderStatus.from(status);
        this.totalPrice = totalPrice;
        this.orderedBy = orderedBy;
        this.orderedByName = orderedByName;
        this.createdAt = createdAt;
    }
}
