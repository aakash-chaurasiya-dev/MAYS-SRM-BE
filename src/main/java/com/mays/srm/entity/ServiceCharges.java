package com.mays.srm.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Service_Charges")
public class ServiceCharges {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "charge_id")
    private Integer chargeId;

    @ManyToOne
    @JoinColumn(name = "device_type")
    private DeviceType deviceType;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @ManyToOne
    @JoinColumn(name = "model_id")
    private DeviceModel model;

    @Column(name = "descr", columnDefinition = "TEXT")
    private String descr;

    @Column(name = "amount")
    private BigDecimal amount;
}
