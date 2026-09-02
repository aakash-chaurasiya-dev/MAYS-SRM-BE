package com.mays.srm.enquiry.dto.request;

import lombok.Data;

@Data
public class InwardRequestDTO {
    private Integer userId;
    private String customerName;
    private String mobileNo;
    private String emailId;
    private String address;
    private String serialNo;
    private Integer deviceTypeId;
    private Integer brandId;
    private Integer modelId;
    private String customModelName;
    private String inwardRemarks;
    private Integer createdByEmployeeId;
}
