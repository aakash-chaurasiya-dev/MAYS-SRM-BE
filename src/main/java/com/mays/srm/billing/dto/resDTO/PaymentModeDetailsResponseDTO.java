package com.mays.srm.billing.dto.resDTO;
import lombok.Data;

@Data
public class PaymentModeDetailsResponseDTO {
    private Integer payModeId;
    private String paymentMode;
    private String description;
}
