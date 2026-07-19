package com.mays.srm.device.entities;
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

    @jakarta.persistence.Column(name = "is_locked")
    private Boolean isLocked = false;

    @org.hibernate.annotations.CreationTimestamp
    @jakarta.persistence.Column(name = "insert_date", updatable = false)
    private java.util.Date insertDate;

    @org.hibernate.annotations.UpdateTimestamp
    @jakarta.persistence.Column(name = "last_update_date")
    private java.util.Date lastUpdateDate;
}
