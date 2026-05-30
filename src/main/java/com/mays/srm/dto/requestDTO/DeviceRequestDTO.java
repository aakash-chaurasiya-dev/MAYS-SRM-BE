package com.mays.srm.dto.requestDTO;

import lombok.Data;

@Data
public class DeviceRequestDTO {
    private String serialNo;
    private Integer modelId; // Used to link to a DeviceModel
}
