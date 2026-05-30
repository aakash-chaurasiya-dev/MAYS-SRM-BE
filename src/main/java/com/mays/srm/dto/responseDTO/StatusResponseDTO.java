package com.mays.srm.dto.responseDTO;

import lombok.Data;

@Data
public class StatusResponseDTO {
    private Integer statusId;
    private String statusName;
    private Integer statusFlg;
    private String statusDescription;
    private String statusType;
}
