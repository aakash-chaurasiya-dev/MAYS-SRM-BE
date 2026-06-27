package com.mays.srm.device.dto.request;
import com.mays.srm.device.entities.DeviceModel;
import lombok.Data;

@Data
public class DeviceRequestDTO {
    private String serialNo;
    private Integer modelId; // Used to link to a DeviceModel
}
