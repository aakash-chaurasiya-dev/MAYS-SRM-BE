package com.mays.srm.dto.responseDTO;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ServiceChargesResponseDTO {
    private Integer chargeId;
    private String brandName;
    private String descr;
    private BigDecimal amount;
}
