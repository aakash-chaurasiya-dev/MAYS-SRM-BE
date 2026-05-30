package com.mays.srm.dto.responseDTO;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BillingResponseDTO {
    private Integer billingId;
    private Integer ticketId;
    private String chargeTypeName;
    private String productName;
    private String serviceChargeDescription;
    private String paymentModeName;
    private BigDecimal amount;
    private String statusName;
    private LocalDateTime billingDate;
}
