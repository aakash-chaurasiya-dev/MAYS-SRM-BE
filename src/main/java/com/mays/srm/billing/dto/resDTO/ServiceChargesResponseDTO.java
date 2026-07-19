package com.mays.srm.billing.dto.resDTO;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ServiceChargesResponseDTO {
    private Integer chargeId;
    private String brandName;
    private String descr;
    private BigDecimal amount;

    private Boolean isLocked;
    private java.util.Date insertDate;
    private java.util.Date lastUpdateDate;
}
