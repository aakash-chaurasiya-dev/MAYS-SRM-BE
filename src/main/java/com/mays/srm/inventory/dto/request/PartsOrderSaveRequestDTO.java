package com.mays.srm.inventory.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class PartsOrderSaveRequestDTO {
    private Integer orderId;
    private Integer ticketPartId;
    private Integer ticketId;
    private Integer partCatId;
    private Integer quantity;
    private String remarks;
    private List<PartsMasterLineRequestDTO> lines;
}
