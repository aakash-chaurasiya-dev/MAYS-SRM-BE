package com.mays.srm.ticket.dto.resDTO;

import lombok.Data;
import java.util.Date;

@Data
public class WarrantyTypeResponseDTO {
    private Integer warrantyTypeId;
    private String warrantyTypeName;
    private Integer ticketTypeId;
    private String ticketTypeName;
    private String warrantyTypeDescription;
    private Date insertDate;
    private Date updateDate;
    private Boolean isLocked;
}
