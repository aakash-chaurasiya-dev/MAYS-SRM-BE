package com.mays.srm.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntryReportResponseDTO {
    private Integer entryNo;
    private Integer userId;
    private String userName;
    private String reason;
    private Date entryDate;
}
