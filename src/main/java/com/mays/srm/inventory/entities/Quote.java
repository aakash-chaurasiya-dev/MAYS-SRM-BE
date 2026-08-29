package com.mays.srm.inventory.entities;

import com.mays.srm.inventory.enums.QuoteStatus;
import com.mays.srm.ticket.entities.Ticket;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "quotes")
public class Quote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quote_id")
    private Integer quoteId;

    @Column(name = "quote_no")
    private String quoteNo;

    @ManyToOne
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @ManyToOne
    @JoinColumn(name = "part_cat_id", nullable = false)
    private ProductList productList;

    @ManyToOne
    @JoinColumn(name = "ticket_part_id")
    private TicketPart ticketPart;

    @Column(name = "sales_price")
    private BigDecimal salesPrice;

    @Column(name = "currency")
    private String currency = "INR";

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "subject")
    private String subject;

    @Column(name = "body", columnDefinition = "TEXT")
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private QuoteStatus status = QuoteStatus.DRAFT;

    @Column(name = "valid_until")
    private LocalDate validUntil;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "sent_by")
    private Integer sentBy;

    @Column(name = "created_by")
    private Integer createdBy;

    @Column(name = "updated_by")
    private Integer updatedBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
