package com.mays.srm.inventory.dto.request;

import com.mays.srm.inventory.enums.PartsOrderStatus;
import lombok.Data;

@Data
public class PartsOrderStatusRequestDTO {
    private PartsOrderStatus status;
    private String cancelReason;
}
