package com.mays.srm.inventory.entities;

import com.mays.srm.inventory.enums.PartsMasterSource;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "in_stock_part")
public class InStockPart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "individual_part_id")
    private Integer individualPartId;

    @ManyToOne
    @JoinColumn(name = "part_cat_id", nullable = false)
    private ProductList productList;

    @Column(name = "part_sr_no")
    private String partSrNo;

    @Column(name = "barcode")
    private String barcode;

    @Enumerated(EnumType.STRING)
    @Column(name = "source")
    private PartsMasterSource source = PartsMasterSource.MARKET;

    @Column(name = "received")
    private Boolean received = false;

    @Column(name = "received_at")
    private LocalDateTime receivedAt;

    @Column(name = "ordered_by")
    private Integer orderedBy;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "is_active")
    private Boolean isActive = true;

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
