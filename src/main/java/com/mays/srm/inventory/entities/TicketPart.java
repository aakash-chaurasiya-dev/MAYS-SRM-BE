package com.mays.srm.inventory.entities;

import com.mays.srm.inventory.enums.TicketPartStatus;
import com.mays.srm.ticket.entities.Ticket;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "ticket_parts")
public class TicketPart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_part_id")
    private Integer ticketPartId;

    @ManyToOne
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @ManyToOne
    @JoinColumn(name = "part_cat_id", nullable = false)
    private ProductList productList;

    @Column(name = "quantity")
    private Integer quantity = 1;

    @Column(name = "remark", columnDefinition = "TEXT")
    private String remark;

    @Column(name = "manager_approval")
    private Boolean managerApproval;

    @Column(name = "manager_approved_at")
    private LocalDateTime managerApprovedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "part_status")
    private TicketPartStatus partStatus = TicketPartStatus.REQUESTED;

    @Column(name = "send_quotes")
    private Boolean sendQuotes = false;

    @Column(name = "quotes_sent_at")
    private LocalDateTime quotesSentAt;

    @Column(name = "customer_approval")
    private Boolean customerApproval;

    @Column(name = "customer_approved_at")
    private LocalDateTime customerApprovedAt;

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
