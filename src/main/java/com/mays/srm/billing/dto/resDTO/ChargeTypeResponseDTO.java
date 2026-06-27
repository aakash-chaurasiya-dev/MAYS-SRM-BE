package com.mays.srm.billing.dto.resDTO;
import lombok.Data;

@Data
public class ChargeTypeResponseDTO {
    private Integer chargeTypeId;
    private String chargeName;
    private String chargeDescription;
}
