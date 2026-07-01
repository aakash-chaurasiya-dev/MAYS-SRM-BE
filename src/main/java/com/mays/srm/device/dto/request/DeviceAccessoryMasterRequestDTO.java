package com.mays.srm.device.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeviceAccessoryMasterRequestDTO {
    @NotBlank(message = "Accessory name is required")
    private String accessoryName;
    
    private String description;
    
    @NotNull(message = "Device type ID is required")
    private Integer deviceTypeId;
}
