package com.mays.srm.dto.requestDTO;

import lombok.Data;

@Data
public class DeviceModelRequestDTO {
    private String modelName;
    private String modelDescription;
    private Integer brandId; // Used to link to a Brand
}
