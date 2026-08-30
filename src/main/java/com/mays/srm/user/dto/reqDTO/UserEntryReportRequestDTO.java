package com.mays.srm.user.dto.reqDTO;

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
    private String entryType;
    private Integer enquiryId;
    private Integer ticketId;
    private Integer inwardId;
    private Integer outwardId;
}

