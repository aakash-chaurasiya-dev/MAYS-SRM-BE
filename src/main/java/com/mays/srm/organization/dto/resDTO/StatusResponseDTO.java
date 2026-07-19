package com.mays.srm.organization.dto.resDTO;
import lombok.Data;

@Data
public class StatusResponseDTO {
    private Integer statusId;
    private String statusName;
    private Integer statusFlg;
    private String statusDescription;
    private String statusType;

    private Boolean isLocked;
    private java.util.Date insertDate;
    private java.util.Date lastUpdateDate;
}
