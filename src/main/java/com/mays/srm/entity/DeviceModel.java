package com.mays.srm.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table( name = "device_model",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"model_name", "brand_id"})
        })
public class DeviceModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "model_id")
    private Integer modelId;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @Column(name = "model_name")
    private String modelName;

    @Column(name = "model_description")
    private String modelDescription;
}
