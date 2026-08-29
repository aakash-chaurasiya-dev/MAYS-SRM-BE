package com.mays.srm.inventory.entities;

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
@Table(name = "vendor_damage_part_return")
public class VendorDamagePartReturn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vendor_damage_part_return_id")
    private Integer vendorDamagePartReturnId;

    @ManyToOne
    @JoinColumn(name = "individual_part_id", nullable = false)
    private PartsMaster partsMaster;

    @ManyToOne
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @Column(name = "return_part_sr_no")
    private String returnPartSrNo;

    @Column(name = "remark", columnDefinition = "TEXT")
    private String remark;

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
