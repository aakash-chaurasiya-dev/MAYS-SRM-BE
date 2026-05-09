package com.mays.srm.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "brand",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"brand_name", "device_type_id"})
        })
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "brand_id")
    private Integer brandId;

    @ManyToOne
    @JoinColumn(name = "device_type_id")
    private DeviceType deviceType;

    @Column(name = "brand_name")
    private String brandName;

    @Column(name = "brand_description")
    private String brandDescription;
}
