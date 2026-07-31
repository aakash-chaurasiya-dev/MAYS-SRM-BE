package com.mays.srm.user.entities;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vendor")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Vendor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 250)
    private String address;

    @Column(nullable = false, length = 120, unique = true)
    private String email;

    @Column(nullable = false, length = 30, unique = true)
    private String mobileNo;

    @Column(nullable = false, length = 255)
    private String password;   // store BCrypt hash

    @Column(nullable = false, length = 50)
    private String roleName;        // e.g. ROLE_VENDOR

    @Builder.Default
    @Column(nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime insertedAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(length = 150)
    private String referredBy;      // free‑text source
}
