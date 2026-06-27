package com.mays.srm.inventory.entities;
import com.mays.srm.device.entities.Brand;
import com.mays.srm.device.entities.DeviceType;
import com.mays.srm.organization.entities.Branch;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Inventory")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "product_name")
    private String productName;

    @ManyToOne
    @JoinColumn(name = "device_type")
    private DeviceType deviceType;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @Column(name = "specification", columnDefinition = "TEXT")
    private String specification;

    @Column(name = "descr", columnDefinition = "TEXT")
    private String descr;

    @Column(name = "selling_price")
    private BigDecimal sellingPrice;

    @Column(name = "buying_price")
    private BigDecimal buyingPrice;

    @Column(name = "stock")
    private Integer stock;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @UpdateTimestamp
    @Column(name = "last_updation_date")
    private LocalDateTime lastUpdationDate;
}
