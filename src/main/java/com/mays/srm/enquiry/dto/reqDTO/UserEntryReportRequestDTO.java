package com.mays.srm.enquiry.dto.reqDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntryReportRequestDTO {
    private Integer userId;
    private String reason;
    private Integer enquiryId;
}

