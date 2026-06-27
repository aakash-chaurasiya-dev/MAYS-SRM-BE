package com.mays.srm.billing.entities;
import com.mays.srm.device.entities.Brand;
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
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @Column(name = "descr", columnDefinition = "TEXT")
    private String descr;

    @Column(name = "amount")
    private BigDecimal amount;
}
