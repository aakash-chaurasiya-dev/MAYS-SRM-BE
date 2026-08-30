package com.mays.srm.enquiry.dto.request;
import lombok.Data;

@Data
public class EnquiryRequestDTO {
    private Integer userId;
    private String serialNo;
    private Integer deviceTypeId;
    private Integer brandId;
    private Integer modelId;
    private String customModelName;
    private String enquiryFor;
    private String queryText;
    private String remark;
    private Integer statusId;
    private String customerName;
    private String mobileNo;
    private String emailId;
    private String address;
}

