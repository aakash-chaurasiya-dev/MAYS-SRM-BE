package com.mays.srm.device.dto.request;
import com.mays.srm.device.entities.DeviceType;
import lombok.Data;

@Data
public class BrandRequestDTO {
    private String brandName;
    private String brandDescription;
    private Integer deviceTypeId; // Used to link to a DeviceType
}
