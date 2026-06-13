package com.mays.srm.dto.responseDTO;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BillingResponseDTO {
    private Integer billingId;
    private Integer ticketId;
    private Integer chargeTypeId;
    private String chargeTypeName;
    private Integer productId;
    private String productName;
    private Integer serviceChargeId;
    private String serviceChargeDescription;
    private Integer paymentModeId;
    private String paymentModeName;
    private BigDecimal amount;
    private Integer statusId;
    private String statusName;
    private LocalDateTime billingDate;
    private String customerName;
}
