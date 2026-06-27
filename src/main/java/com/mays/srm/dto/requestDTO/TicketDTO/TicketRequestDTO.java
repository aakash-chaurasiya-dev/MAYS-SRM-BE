package com.mays.srm.dto.requestDTO.TicketDTO;

import lombok.Data;

@Data
public class TicketRequestDTO {
    private String userRefNo; // UserMaster ID
    private Integer ticketTypeId;
    private Integer ticketStatusId;
    private String emailId;
    private String deviceSerialNo; // Device ID
    private String ticketDescription;
    private Integer ticketBranchId;
    private Integer employeeId;
    private String warrantyType;
    private String priority;
    private String remarks; // Added for logging purposes during updates
    private Integer modifiedByEmployeeId; // Employee making the update

    // Optional field for creating a new device along with the ticket
    private Integer deviceModelId;
    private String customModelName;
    private Integer brandId;
}
