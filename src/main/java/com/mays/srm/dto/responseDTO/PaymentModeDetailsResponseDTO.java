package com.mays.srm.dto.responseDTO;

import lombok.Data;

@Data
public class PaymentModeDetailsResponseDTO {
    private Integer payModeId;
    private String paymentMode;
    private String description;
}
