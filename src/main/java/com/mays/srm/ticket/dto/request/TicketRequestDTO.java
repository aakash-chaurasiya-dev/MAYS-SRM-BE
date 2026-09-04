package com.mays.srm.ticket.dto.request;

import lombok.Data;
import java.util.List;
import java.time.LocalDateTime;

@Data
public class TicketRequestDTO {
    private String userRefNo; // UserMaster ID
    private Integer ticketTypeId;
    private Integer ticketStatusId;
//    private String emailId;
    private String deviceSerialNo; // Device ID
    private String ticketDescription;
    private Integer ticketBranchId;
    private Integer employeeId;
    private Integer referredCategoryId;
    private String referredCategoryDecriptionTicket;
    private Integer warrantyTypeId;
    private String priority;
    private String remarks; // Added for logging purposes during updates
    private Integer modifiedByEmployeeId; // Employee making the update
    private LocalDateTime targetDate;
    private LocalDateTime closedDate;

    // Optional field for creating a new device along with the ticket
    private Integer deviceModelId;
    private String customModelName;
    private Integer brandId;

    // Optional field for syncing accessories with the ticket
    private List<Integer> accessoryIds;

    private Integer vendorId;
    private Integer vendorUserId;
    private Integer parentTicketId;
}
