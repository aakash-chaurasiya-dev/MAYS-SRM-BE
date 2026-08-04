package com.mays.srm.inventory.entities;

import com.mays.srm.inventory.enums.InventoryLogReason;
import com.mays.srm.organization.entities.Branch;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "inventory_log")
public class InventoryLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Integer logId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Inventory product;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @Column(name = "change_qty", nullable = false)
    private Integer changeQty;

    @Column(name = "balance_after", nullable = false)
    private Integer balanceAfter;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason", length = 20, nullable = false)
    private InventoryLogReason reason;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Parts order;

    @Column(name = "created_by")
    private String createdBy;

    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;
}
