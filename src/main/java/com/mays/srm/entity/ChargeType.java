package com.mays.srm.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "charge_type")
public class ChargeType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Charge_type_id")
    private Integer chargeTypeId;

    @Column(name = "Charge_name", nullable = false, length = 100)
    private String chargeName;

    @Column(name = "Charge_description", length = 255)
    private String chargeDescription;
}
