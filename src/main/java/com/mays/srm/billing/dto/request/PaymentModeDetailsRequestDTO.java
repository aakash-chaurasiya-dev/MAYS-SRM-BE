package com.mays.srm.billing.dto.request;
import lombok.Data;

@Data
public class PaymentModeDetailsRequestDTO {
    private String paymentMode;
    private String description;
}
