package com.mays.srm.dto.responseDTO.TicketDTO;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TicketDashboardResponseDTO {
    private Integer ticketId;
    private String userFirstName;
    private String userLastName;
    private String deviceSerialNo;
    private String branchName;
    private String ticketStatusName;
    private String departmentName; // Added field
    private LocalDateTime createdDate;

    // Required by JPA and some serialization
    public TicketDashboardResponseDTO() {
    }

    // Constructor used by JPA JPQL Projection to directly map the specific columns
    // to this DTO
    public TicketDashboardResponseDTO(Integer ticketId, String userFirstName, String userLastName,
            String deviceSerialNo, String branchName, String ticketStatusName,
            String departmentName, LocalDateTime createdDate) {
        this.ticketId = ticketId;
        this.userFirstName = userFirstName != null ? userFirstName : "N/A";
        this.userLastName = userLastName != null ? userLastName : "";
        this.deviceSerialNo = deviceSerialNo != null ? deviceSerialNo : "N/A";
        this.branchName = branchName != null ? branchName : "N/A";
        this.ticketStatusName = ticketStatusName != null ? ticketStatusName : "N/A";
        this.departmentName = departmentName != null ? departmentName : "N/A";
        this.createdDate = createdDate;
    }
}
