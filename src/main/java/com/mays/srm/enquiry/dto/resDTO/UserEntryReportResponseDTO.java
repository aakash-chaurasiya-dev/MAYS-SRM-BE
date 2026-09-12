package com.mays.srm.enquiry.dto.resDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntryReportResponseDTO {
    private Integer entryNo;
    private Integer userId;
    private String userName;
    private String reason;
    private Integer enquiryId;
    private LocalDateTime entryDate;
    private String brandName;
    private String modelName;
    private String serialNo;
    private String deviceTypeName;
}
