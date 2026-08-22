package com.mays.srm.inventory.entities;

import com.mays.srm.device.entities.Brand;
import com.mays.srm.device.entities.DeviceType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "product_list")
public class ProductList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "part_cat_id")
    private Integer partCatId;

    @ManyToOne
    @JoinColumn(name = "device_type_id")
    private DeviceType deviceType;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @Column(name = "part_name", nullable = false)
    private String partName;

    @Column(name = "sku")
    private String sku;

    @Column(name = "hsn_code")
    private String hsnCode;

    @Column(name = "specification", columnDefinition = "TEXT")
    private String specification;

    @Column(name = "descr", columnDefinition = "TEXT")
    private String descr;

    @Column(name = "is_active")
    private Boolean isActive = true;

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
