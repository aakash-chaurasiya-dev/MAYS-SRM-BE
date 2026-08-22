package com.mays.srm.inventory.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "part_price")
public class PartPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "part_price_id")
    private Integer partPriceId;

    @Column(name = "individual_part_id", nullable = false)
    private Integer individualPartId;

    @ManyToOne
    @JoinColumn(name = "part_cat_id", nullable = false)
    private ProductList productList;

    @Column(name = "sales_price")
    private BigDecimal salesPrice;

    @Column(name = "purchase_price")
    private BigDecimal purchasePrice;

    @Column(name = "currency")
    private String currency = "INR";

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

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
