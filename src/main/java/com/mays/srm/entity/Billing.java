package com.mays.srm.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Billing")
public class Billing {

    @Id
    @Column(name = "ticket_id")
    private Integer ticketId;

    @OneToOne
    @JoinColumn(name = "ticket_id")
    @MapsId
    private Ticket ticket;

    @Column(name = "gst")
    private BigDecimal gst;

    @Column(name = "delivery_charges")
    private BigDecimal deliveryCharges;

    @Column(name = "pickup_charges")
    private BigDecimal pickupCharges;

    @Column(name = "payment_mode")
    private String paymentMode;

    @Column(name = "misc_charges")
    private BigDecimal miscCharges;

    @Column(name = "total_amt")
    private BigDecimal totalAmt;

    @ManyToOne
    @JoinColumn(name = "service_charge_id")
    private ServiceCharges serviceCharge;

    @ManyToOne
    @JoinColumn(name = "part_charge_id")
    private Inventory partCharge;
}
