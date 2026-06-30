package com.mays.srm.device.entities;

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
@Table(name = "accessory_master")
public class DeviceAccessoryMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "accessory_id")
    private Integer accessoryId;

    @Column(name = "accessory_name", nullable = false, length = 100)
    private String accessoryName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "device_type_id", nullable = false)
    private DeviceType deviceType;

    @CreationTimestamp
    @Column(name = "insert_date", updatable = false)
    private Date insertDate;

    @UpdateTimestamp
    @Column(name = "last_update_date")
    private Date lastUpdateDate;
}
