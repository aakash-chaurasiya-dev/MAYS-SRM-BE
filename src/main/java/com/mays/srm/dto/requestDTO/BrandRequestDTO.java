package com.mays.srm.dto.requestDTO;

import lombok.Data;

@Data
public class BrandRequestDTO {
    private String brandName;
    private String brandDescription;
    private Integer deviceTypeId; // Used to link to a DeviceType
}
