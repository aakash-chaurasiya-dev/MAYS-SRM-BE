package com.mays.srm.dto.responseDTO;

import lombok.Data;

@Data
public class DeviceResponseDTO {
    private String serialNo;
    private String modelName; // For context
    private String brandName; // For context, through model
}
