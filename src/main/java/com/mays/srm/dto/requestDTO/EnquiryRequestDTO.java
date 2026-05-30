package com.mays.srm.dto.requestDTO;

import lombok.Data;

@Data
public class EnquiryRequestDTO {
    private Integer userId;
    private String serialNo;
    private Integer brandId;
    private String enquiryFor;
    private String queryText;
    private String remark;
    private Integer statusId;
}
