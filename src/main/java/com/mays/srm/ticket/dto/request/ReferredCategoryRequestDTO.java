package com.mays.srm.ticket.dto.request;

import lombok.Data;

@Data
public class ReferredCategoryRequestDTO {
    private String referredCategoryName;
    private String referredCategoryDescription;
    private Boolean isLocked;
}
