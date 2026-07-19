package com.mays.srm.device.dto.resDTO;
import lombok.Data;

@Data
public class DeviceModelResponseDTO {
    private Integer modelId;
    private String modelName;
    private String modelDescription;
    private String brandName; // For context
    private Integer deviceTypeId; // For context
    private String deviceTypeName; // For context

    private Boolean isLocked;
    private java.util.Date insertDate;
    private java.util.Date lastUpdateDate;
}
