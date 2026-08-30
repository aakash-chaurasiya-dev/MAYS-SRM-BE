package com.mays.srm.enquiry.dto.request;

import lombok.Data;

@Data
public class OutwardRequestDTO {
    private Integer ticketId;
    private String outwardRemarks;
    private String handoverToName;
    private String handoverToPhone;
    private Integer createdByEmployeeId;
}
