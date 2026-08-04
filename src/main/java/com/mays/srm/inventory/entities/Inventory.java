package com.mays.srm.inventory.entities;
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

    @Column(name = "sku")
    private String sku;

    @ManyToOne
    @JoinColumn(name = "device_type")
    private DeviceType deviceType;

    @Column(name = "specification", columnDefinition = "TEXT")
    private String specification;

    @Column(name = "descr", columnDefinition = "TEXT")
    private String descr;

    @Column(name = "selling_price")
    private BigDecimal sellingPrice;

    @Column(name = "buying_price")
    private BigDecimal buyingPrice;

    @Column(name = "stock")
    private Integer stock = 0;

    @Column(name = "min_stock")
    private Integer minStock;

    @Column(name = "hsn_code")
    private String hsnCode;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @UpdateTimestamp
    @Column(name = "last_updation_date")
    private LocalDateTime lastUpdationDate;
}
