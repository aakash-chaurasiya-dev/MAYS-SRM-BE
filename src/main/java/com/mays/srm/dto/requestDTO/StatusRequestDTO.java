package com.mays.srm.dto.requestDTO;

import lombok.Data;

@Data
public class StatusRequestDTO {
    private String statusName;
    private Integer statusFlg;
    private String statusDescription;
    private String statusType;
}
