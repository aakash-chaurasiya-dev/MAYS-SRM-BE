package com.mays.srm.device.entities;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Device_Type")
public class DeviceType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "device_type_id")
    private Integer deviceTypeId;

    @Column(name = "device_type_name")
    private String deviceTypeName;

    @Column(name = "device_type_description")
    private String deviceTypeDescription;

    @jakarta.persistence.Column(name = "is_locked")
    private Boolean isLocked = false;

    @org.hibernate.annotations.CreationTimestamp
    @jakarta.persistence.Column(name = "insert_date", updatable = false)
    private java.util.Date insertDate;

    @org.hibernate.annotations.UpdateTimestamp
    @jakarta.persistence.Column(name = "last_update_date")
    private java.util.Date lastUpdateDate;
}
