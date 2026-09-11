package com.mays.srm.enquiry.dto.reqDTO;
import lombok.Data;

@Data
public class EnquiryRequestDTO {
    private Integer userId;
    private String enquiryFor;
    private String queryText;
    private String remark;


    private String serialNo;
    private Integer brandId;
    private Integer modelId;
    private String customModelName;
    private int action;
}

