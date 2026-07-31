package com.mays.srm.ticket.dto.resDTO;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TicketResponseDTO {
    private Integer ticketId;
    private String userFirstName;
    private String userLastName;
    private String userMobileNo;
    private Integer userId; // Added field to map the customer user ID
    private String ticketTypeName;
    private Integer ticketStatusId; // Added field
    private String ticketStatusName;
    private String emailId;
    private String deviceSerialNo;
    private String deviceModelName;
    private String deviceTypeName;
    private String deviceBrandName;
    private String ticketDescription;
    private String branchName;
    private Integer branchId;
    private Integer employeeId; // Added field
    private String employeeName;
    private LocalDateTime targetDate;
    private LocalDateTime closedDate;
    private Integer deviceModelId;
    private Integer deviceTypeId;
    private Integer deviceBrandId;
    private Integer departmentId; // Added field
    private String departmentName; // Added field
    private Integer referredCategoryId;
    private String referredCategoryName;
    private String referredCategoryDecriptionTicket;
    private Integer warrantyTypeId;
    private String warrantyTypeName;
    private String priority;
    private LocalDateTime createdDate;
    private LocalDateTime lastUpdatedDate;
    private Integer modNo;

    private Integer vendorId;
    private String vendorName;
    private Integer vendorUserId;
    private String vendorUserName;
    private String vendorUserMobileNo;
    private String customerAddress;
    private Integer parentTicketId;

    public Integer getTicketId() {
        return this.ticketId;
    }

    public void setTicketId(Integer ticketId) {
        this.ticketId = ticketId;
    }
}
