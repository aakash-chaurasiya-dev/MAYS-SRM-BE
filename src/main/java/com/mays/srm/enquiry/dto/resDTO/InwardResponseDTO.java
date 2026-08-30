package com.mays.srm.enquiry.dto.resDTO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class InwardResponseDTO {
    private Integer inwardId;
    private Integer userId;
    private String userName;
    private String serialNo;
    private Integer deviceTypeId;
    private String deviceTypeName;
    private Integer brandId;
    private String brandName;
    private Integer modelId;
    private String deviceModelName;
    private String customModelName;
    private String inwardRemarks;
    private Integer createdByEmployeeId;
    private LocalDateTime createdDate;
}
