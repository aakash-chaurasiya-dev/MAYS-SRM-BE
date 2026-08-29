package com.mays.srm.inventory.dto.resDTO;

import com.mays.srm.inventory.enums.PartsOrderStatus;
import com.mays.srm.inventory.enums.QuoteStatus;
import com.mays.srm.inventory.enums.TicketPartStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TicketPartResponseDTO {

    private Integer ticketPartId;
    private Integer ticketId;
    private Integer partCatId;
    private String partName;
    private String sku;
    private String deviceTypeName;
    private String brandName;
    private Integer quantity;
    private String remark;
    private Boolean managerApproval;
    private LocalDateTime managerApprovedAt;
    private TicketPartStatus partStatus;
    private Boolean sendQuotes;
    private LocalDateTime quotesSentAt;
    private Boolean customerApproval;
    private LocalDateTime customerApprovedAt;
    private Integer quoteId;
    private QuoteStatus quoteStatus;
    private Integer orderId;
    private PartsOrderStatus orderStatus;
    private Boolean canQuote;
    private Boolean canOrder;
    private Integer createdBy;
    private String createdByName;
    private Integer updatedBy;
    private String updatedByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TicketPartResponseDTO(
            Integer ticketPartId,
            Integer ticketId,
            Integer partCatId,
            String partName,
            String sku,
            String deviceTypeName,
            String brandName,
            Integer quantity,
            String remark,
            Boolean managerApproval,
            LocalDateTime managerApprovedAt,
            String partStatus,
            Boolean sendQuotes,
            LocalDateTime quotesSentAt,
            Boolean customerApproval,
            LocalDateTime customerApprovedAt,
            Integer quoteId,
            String quoteStatus,
            Integer orderId,
            String orderStatus,
            Boolean canQuote,
            Boolean canOrder,
            Integer createdBy,
            String createdByName,
            Integer updatedBy,
            String updatedByName,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.ticketPartId = ticketPartId;
        this.ticketId = ticketId;
        this.partCatId = partCatId;
        this.partName = partName;
        this.sku = sku;
        this.deviceTypeName = deviceTypeName;
        this.brandName = brandName;
        this.quantity = quantity;
        this.remark = remark;
        this.managerApproval = managerApproval;
        this.managerApprovedAt = managerApprovedAt;
        this.partStatus = TicketPartStatus.from(partStatus);
        this.sendQuotes = sendQuotes;
        this.quotesSentAt = quotesSentAt;
        this.customerApproval = customerApproval;
        this.customerApprovedAt = customerApprovedAt;
        this.quoteId = quoteId;
        this.quoteStatus = QuoteStatus.from(quoteStatus);
        this.orderId = orderId;
        this.orderStatus = PartsOrderStatus.from(orderStatus);
        this.canQuote = canQuote;
        this.canOrder = canOrder;
        this.createdBy = createdBy;
        this.createdByName = createdByName;
        this.updatedBy = updatedBy;
        this.updatedByName = updatedByName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
