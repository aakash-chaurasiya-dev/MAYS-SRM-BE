package com.mays.srm.device.dto.resDTO;

import lombok.Data;
import java.util.Date;

@Data
public class DeviceAccessoryMasterResponseDTO {
    private Integer accessoryId;
    private String accessoryName;
    private String description;
    private Integer deviceTypeId;
    private String deviceTypeName;
    private Date insertDate;
    private Date lastUpdateDate;
}
