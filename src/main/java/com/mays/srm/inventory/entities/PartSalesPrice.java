package com.mays.srm.inventory.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "part_sales_price")
public class PartSalesPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sales_price_id")
    private Integer salesPriceId;

    @ManyToOne
    @JoinColumn(name = "part_cat_id", nullable = false)
    private ProductList productList;

    @Column(name = "sales_price", nullable = false)
    private BigDecimal salesPrice;

    @Column(name = "currency")
    private String currency = "INR";

    @Column(name = "effective_from")
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

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
