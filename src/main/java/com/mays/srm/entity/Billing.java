package com.mays.srm.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "billing")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Billing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "billing_id")
    private Integer billingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Inventory product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_charge_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private ServiceCharges serviceCharge;

    @Column(name = "payment_mode", length = 50)
    private String paymentMode;

    @Column(name = "amount")
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Charge_type_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private ChargeType chargeType;
}