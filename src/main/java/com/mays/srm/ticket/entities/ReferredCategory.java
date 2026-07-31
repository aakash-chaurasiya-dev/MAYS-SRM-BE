package com.mays.srm.ticket.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "referred_category")
public class ReferredCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "referred_category_id")
    private Integer referredCategoryId;

    @Column(name = "referred_category_name", nullable = false)
    private String referredCategoryName;

    @Column(name = "referred_category_description", columnDefinition = "TEXT")
    private String referredCategoryDescription;

    @Column(name = "is_locked")
    private Boolean isLocked = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;
}
