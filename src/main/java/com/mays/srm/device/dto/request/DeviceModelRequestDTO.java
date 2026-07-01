package com.mays.srm.device.dto.request;
import com.mays.srm.device.entities.Brand;
import lombok.Data;

@Data
public class DeviceModelRequestDTO {
    private String modelName;
    private String modelDescription;
    private Integer brandId; // Used to link to a Brand
}
