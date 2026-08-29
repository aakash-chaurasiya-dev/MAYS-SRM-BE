package com.mays.srm.inventory.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class QuoteRequestDTO {
    private Integer quoteId;
    private Integer ticketId;
    private Integer partCatId;
    private Integer ticketPartId;
    private BigDecimal salesPrice;
    private String description;
    private String subject;
    private String body;
    private LocalDate validUntil;
}
