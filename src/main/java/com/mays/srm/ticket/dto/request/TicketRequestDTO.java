package com.mays.srm.ticket.dto.request;
import com.mays.srm.device.entities.Device;
import com.mays.srm.user.entities.Employee;
import com.mays.srm.user.entities.UserMaster;
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
