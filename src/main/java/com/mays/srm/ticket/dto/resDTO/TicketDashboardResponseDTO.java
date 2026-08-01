package com.mays.srm.ticket.dto.resDTO;
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
    private LocalDateTime targetDate;
    private Integer employeeId;
    private String employeeName;

    private Integer vendorId;
    private String vendorName;
    private Integer vendorUserId;
    private String vendorUserName;
    private Integer parentTicketId;

    // Required by JPA and some serialization
    public TicketDashboardResponseDTO() {
    }

    // Constructor used by JPA JPQL Projection to directly map the specific columns
    // to this DTO
    public TicketDashboardResponseDTO(Integer ticketId, String userFirstName, String userLastName,
            String deviceSerialNo, String branchName, String ticketStatusName,
            String departmentName, LocalDateTime createdDate, LocalDateTime targetDate, 
            Integer employeeId, String employeeName,
            Integer vendorId, String vendorName, Integer vendorUserId, String vendorUserName, Integer parentTicketId) {
        this.ticketId = ticketId;
        this.userFirstName = userFirstName != null ? userFirstName : "N/A";
        this.userLastName = userLastName != null ? userLastName : "";
        this.deviceSerialNo = deviceSerialNo != null ? deviceSerialNo : "N/A";
        this.branchName = branchName != null ? branchName : "N/A";
        this.ticketStatusName = ticketStatusName != null ? ticketStatusName : "N/A";
        this.departmentName = departmentName != null ? departmentName : "N/A";
        this.createdDate = createdDate;
        this.targetDate = targetDate;
        this.employeeId = employeeId;
        this.employeeName = employeeName != null ? employeeName : "Unassigned";
        this.vendorId = vendorId;
        this.vendorName = vendorName;
        this.vendorUserId = vendorUserId;
        this.vendorUserName = vendorUserName;
        this.parentTicketId = parentTicketId;
    }
}
