package com.mays.srm.dto.responseDTO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EnquiryResponseDTO {
    private Integer enquiryId;
    private String userFirstName;
    private String userLastName;
    private LocalDateTime timestamp;
    private String serialNo;
    private String brandName;
    private String enquiryFor;
    private String queryText;
    private String remark;
    private String statusName;
}
