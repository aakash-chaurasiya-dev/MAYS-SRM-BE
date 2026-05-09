package com.mays.srm.dto;

import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonNode;

@Getter
@Setter
public class TicketPatchDTO {
    private String userRefNo; // maps to UserMaster userId
    private Integer ticketType; // maps to TicketType ticketTypeId
    private Integer ticketStatus; // maps to Status statusId
    private String emailId;
    private String serialNo; // maps to Device serialNo
    private String ticketDescription;
    private Integer ticketBranch; // maps to Branch branchId
    private Integer employeeId; // maps to Employee employeeId
    private String warrantyType;

    // Default setters for flat JSON keys
    public void setTicketStatus(Integer ticketStatus) { this.ticketStatus = ticketStatus; }
    public void setUserRefNo(String userRefNo) { this.userRefNo = userRefNo; }
    public void setTicketType(Integer ticketType) { this.ticketType = ticketType; }
    public void setSerialNo(String serialNo) { this.serialNo = serialNo; }
    public void setTicketBranch(Integer ticketBranch) { this.ticketBranch = ticketBranch; }
    public void setEmployeeId(Integer employeeId) { this.employeeId = employeeId; }
    public void setEmailId(String emailId) { this.emailId = emailId; }
    public void setTicketDescription(String ticketDescription) { this.ticketDescription = ticketDescription; }
    public void setWarrantyType(String warrantyType) { this.warrantyType = warrantyType; }

    // Custom setters to handle nested JSON objects from client payload
    @JsonSetter("ticketStatus")
    public void setTicketStatusNode(JsonNode node) {
        if (node.isObject() && node.has("statusId")) {
            this.ticketStatus = node.get("statusId").asInt();
        } else if (node.isNumber()) {
            this.ticketStatus = node.asInt();
        }
    }

    @JsonSetter("userMaster")
    public void setUserMasterNode(JsonNode node) {
        if (node.isObject() && node.has("userId")) {
            this.userRefNo = node.get("userId").asText();
        } else if (node.isTextual()) {
             this.userRefNo = node.asText();
        }
    }

    @JsonSetter("ticketType")
    public void setTicketTypeNode(JsonNode node) {
        if (node.isObject() && node.has("ticketTypeId")) {
            this.ticketType = node.get("ticketTypeId").asInt();
        } else if (node.isNumber()) {
             this.ticketType = node.asInt();
        }
    }
    
    @JsonSetter("device")
    public void setDeviceNode(JsonNode node) {
        if (node.isObject() && node.has("serialNo")) {
            this.serialNo = node.get("serialNo").asText();
        } else if (node.isTextual()) {
             this.serialNo = node.asText();
        }
    }
    
    @JsonSetter("ticketBranch")
    public void setTicketBranchNode(JsonNode node) {
        if (node.isObject() && node.has("branchId")) {
            this.ticketBranch = node.get("branchId").asInt();
        } else if (node.isNumber()) {
             this.ticketBranch = node.asInt();
        }
    }
    
    @JsonSetter("employee")
    public void setEmployeeNode(JsonNode node) {
        if (node.isObject() && node.has("employeeId")) {
            this.employeeId = node.get("employeeId").asInt();
        } else if (node.isNumber()) {
             this.employeeId = node.asInt();
        }
    }
}
