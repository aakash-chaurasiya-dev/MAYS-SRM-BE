package com.mays.srm.inventory.dto.resDTO;

import com.mays.srm.inventory.enums.PartsOrderStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class PartsOrderModalResponseDTO {

    private Integer orderId;
    private Integer ticketId;
    private Integer partCatId;
    private Integer ticketPartId;
    private String partName;
    private String deviceTypeName;
    private String brandName;
    private String sku;
    private Integer quantity;
    private PartsOrderStatus status;
    private BigDecimal totalPrice;
    private String currency;
    private String remarks;
    private LocalDateTime orderedAt;
    private LocalDateTime receivedAt;
    private List<PartsMasterLineResponseDTO> lines = new ArrayList<>();

    public PartsOrderModalResponseDTO(
            Integer orderId,
            Integer ticketId,
            Integer partCatId,
            Integer ticketPartId,
            String partName,
            String deviceTypeName,
            String brandName,
            String sku,
            Integer quantity,
            String status,
            BigDecimal totalPrice,
            String currency,
            String remarks,
            LocalDateTime orderedAt,
            LocalDateTime receivedAt) {
        this.orderId = orderId;
        this.ticketId = ticketId;
        this.partCatId = partCatId;
        this.ticketPartId = ticketPartId;
        this.partName = partName;
        this.deviceTypeName = deviceTypeName;
        this.brandName = brandName;
        this.sku = sku;
        this.quantity = quantity;
        this.status = PartsOrderStatus.from(status);
        this.totalPrice = totalPrice;
        this.currency = currency;
        this.remarks = remarks;
        this.orderedAt = orderedAt;
        this.receivedAt = receivedAt;
    }
}
