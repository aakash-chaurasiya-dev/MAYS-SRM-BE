package com.mays.srm.ticket.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "warranty_type")
public class WarrantyType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "warranty_type_id")
    private Integer warrantyTypeId;

    @Column(name = "warranty_type_name", nullable = false)
    private String warrantyTypeName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_type_id")
    private TicketType ticketType;

    @Column(name = "warranty_type_description", columnDefinition = "TEXT")
    private String warrantyTypeDescription;

    @Column(name = "is_locked")
    private Boolean isLocked = false;

    @CreationTimestamp
    @Column(name = "insert_date", updatable = false)
    private Date insertDate;

    @UpdateTimestamp
    @Column(name = "update_date")
    private Date updateDate;
}
