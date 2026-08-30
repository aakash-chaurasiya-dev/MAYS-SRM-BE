package com.mays.srm.enquiry.dto.resDTO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OutwardResponseDTO {
    private Integer outwardId;
    private Integer ticketId;
    private Integer userId;
    private String userName;
    private String serialNo;
    private String outwardStatus;
    private String outwardRemarks;
    private String handoverToName;
    private String handoverToPhone;
    private Integer createdByEmployeeId;
    private LocalDateTime createdDate;
}
