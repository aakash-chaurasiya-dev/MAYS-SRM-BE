package com.mays.srm.device.entities;
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

    @jakarta.persistence.Column(name = "is_locked")
    private Boolean isLocked = false;

    @org.hibernate.annotations.CreationTimestamp
    @jakarta.persistence.Column(name = "insert_date", updatable = false)
    private java.util.Date insertDate;

    @org.hibernate.annotations.UpdateTimestamp
    @jakarta.persistence.Column(name = "last_update_date")
    private java.util.Date lastUpdateDate;
}
