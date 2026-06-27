package com.mays.srm.device.dto.resDTO;
import lombok.Data;

@Data
public class DeviceResponseDTO {
    private String serialNo;
    private String modelName; // For context
    private String brandName; // For context, through model
}
