package com.mays.srm.device.dto.resDTO;
import lombok.Data;

@Data
public class BrandResponseDTO {
    private Integer brandId;
    private String brandName;
    private String brandDescription;
    private String deviceTypeName; // For context
}
