package com.mays.srm.billing.dto.request;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class BillingRequestDTO {
    private Integer billingId;
    private Integer ticketId;
    private Integer chargeTypeId;
    private Integer productId;
    private Integer serviceChargeId;
    private Integer paymentModeId;
    private BigDecimal amount;
    private Integer statusId;
}
