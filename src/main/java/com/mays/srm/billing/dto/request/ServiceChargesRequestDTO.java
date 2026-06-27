package com.mays.srm.billing.dto.request;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ServiceChargesRequestDTO {
    private Integer brandId;
    private String descr;
    private BigDecimal amount;
}
