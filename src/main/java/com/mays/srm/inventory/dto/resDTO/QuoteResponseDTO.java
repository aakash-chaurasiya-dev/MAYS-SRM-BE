package com.mays.srm.inventory.dto.resDTO;

import com.mays.srm.inventory.enums.QuoteStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class QuoteResponseDTO {

    private Integer quoteId;
    private String quoteNo;
    private Integer ticketId;
    private Integer partCatId;
    private Integer ticketPartId;
    private String partName;
    private BigDecimal salesPrice;
    private String description;
    private String subject;
    private String body;
    private QuoteStatus status;
    private LocalDate validUntil;
    private LocalDateTime sentAt;
    private String sentByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public QuoteResponseDTO(
            Integer quoteId,
            String quoteNo,
            Integer ticketId,
            Integer partCatId,
            Integer ticketPartId,
            String partName,
            BigDecimal salesPrice,
            String description,
            String subject,
            String body,
            String status,
            LocalDate validUntil,
            LocalDateTime sentAt,
            String sentByName,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.quoteId = quoteId;
        this.quoteNo = quoteNo;
        this.ticketId = ticketId;
        this.partCatId = partCatId;
        this.ticketPartId = ticketPartId;
        this.partName = partName;
        this.salesPrice = salesPrice;
        this.description = description;
        this.subject = subject;
        this.body = body;
        this.status = QuoteStatus.from(status);
        this.validUntil = validUntil;
        this.sentAt = sentAt;
        this.sentByName = sentByName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
