package com.mays.srm.inventory.entities;

import com.mays.srm.inventory.enums.PartsMasterSource;
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
@Table(name = "parts_master")
public class PartsMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "individual_part_id")
    private Integer individualPartId;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private PartsOrder partsOrder;

    @ManyToOne
    @JoinColumn(name = "part_cat_id", nullable = false)
    private ProductList productList;

    @ManyToOne
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @Column(name = "part_sr_no")
    private String partSrNo;

    @Column(name = "barcode")
    private String barcode;

    @Column(name = "returned_flag")
    private Boolean returnedFlag = false;

    @Column(name = "damaged_flag")
    private Boolean damagedFlag = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false)
    private PartsMasterSource source;

    @Column(name = "vendor_damage_return")
    private Boolean vendorDamageReturn = false;

    @ManyToOne
    @JoinColumn(name = "replaced_id")
    private PartsMaster replacedPart;

    @Column(name = "received")
    private Boolean received = false;

    @Column(name = "received_at")
    private LocalDateTime receivedAt;

    @Column(name = "received_by")
    private Integer receivedBy;

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
