package com.mays.srm.enquiry.dto.resDTO;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EnquiryResponseDTO {
    private Integer enquiryId;

    private Integer userId;
    private String userFirstName;
    private String userLastName;
    private String mobileNo;
    private String emailId;

    private String serialNo;
    private Integer deviceTypeId;
    private String deviceTypeName;
    private Integer brandId;
    private String brandName;
    private Integer modelId;
    private String deviceModelName;

    private String enquiryFor;
    private String queryText;
    private String remark;

    private String status;

    private Integer convertedTicketId;
    private Boolean isConverted;
    private Integer action;             // 0 = Enquiry, 1 = Inward, 2 = Outward
    private LocalDateTime insertDate;   // for "created at" display

}

