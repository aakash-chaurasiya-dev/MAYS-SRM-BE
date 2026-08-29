package com.mays.srm.inventory.entities;

import com.mays.srm.inventory.enums.PartsOrderStatus;
import com.mays.srm.ticket.entities.Ticket;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "parts_order")
public class PartsOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer orderId;

    @ManyToOne
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @ManyToOne
    @JoinColumn(name = "part_cat_id", nullable = false)
    private ProductList productList;

    @ManyToOne
    @JoinColumn(name = "ticket_part_id")
    private TicketPart ticketPart;

    @Column(name = "quantity")
    private Integer quantity = 1;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PartsOrderStatus status = PartsOrderStatus.ORDERED;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @Column(name = "currency")
    private String currency = "INR";

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "cancel_reason", columnDefinition = "TEXT")
    private String cancelReason;

    @Column(name = "ordered_by")
    private Integer orderedBy;

    @Column(name = "ordered_at")
    private LocalDateTime orderedAt;

    @Column(name = "received_by")
    private Integer receivedBy;

    @Column(name = "received_at")
    private LocalDateTime receivedAt;

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
