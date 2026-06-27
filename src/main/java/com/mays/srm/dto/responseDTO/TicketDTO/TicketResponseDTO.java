package com.mays.srm.dto.responseDTO.TicketDTO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TicketResponseDTO {
    private Integer ticketId;
    private String userFirstName;
    private String userLastName;
    private String userMobileNo;
    private String ticketTypeName;
    private String ticketStatusName;
    private String emailId;
    private String deviceSerialNo;
    private String deviceModelName;
    private String deviceTypeName;
    private String deviceBrandName;
    private String ticketDescription;
    private String branchName;
    private String employeeName;
    private String departmentName; // Added field
    private String warrantyType;
    private String priority;
    private LocalDateTime createdDate;
    private LocalDateTime lastUpdatedDate;
    private Integer modNo;
}
