package com.mays.srm.dto.requestDTO;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ServiceChargesRequestDTO {
    private Integer brandId;
    private String descr;
    private BigDecimal amount;
}
