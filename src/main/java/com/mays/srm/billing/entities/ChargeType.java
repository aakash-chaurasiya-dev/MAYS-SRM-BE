package com.mays.srm.billing.entities;
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

    @jakarta.persistence.Column(name = "is_locked")
    private Boolean isLocked = false;

    @org.hibernate.annotations.CreationTimestamp
    @jakarta.persistence.Column(name = "insert_date", updatable = false)
    private java.util.Date insertDate;

    @org.hibernate.annotations.UpdateTimestamp
    @jakarta.persistence.Column(name = "last_update_date")
    private java.util.Date lastUpdateDate;
}
