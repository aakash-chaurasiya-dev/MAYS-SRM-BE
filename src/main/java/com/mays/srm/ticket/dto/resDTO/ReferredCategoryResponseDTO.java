package com.mays.srm.ticket.dto.resDTO;

import lombok.Data;
import java.util.Date;

@Data
public class ReferredCategoryResponseDTO {
    private Integer referredCategoryId;
    private String referredCategoryName;
    private String referredCategoryDescription;
    private Date createdAt;
    private Date updatedAt;
    private Boolean isLocked;
}
