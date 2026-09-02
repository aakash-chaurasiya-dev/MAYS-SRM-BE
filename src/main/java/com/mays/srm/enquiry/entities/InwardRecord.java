package com.mays.srm.enquiry.entities;

import com.mays.srm.device.entities.Brand;
import com.mays.srm.device.entities.DeviceModel;
import com.mays.srm.device.entities.DeviceType;
import com.mays.srm.user.entities.UserMaster;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "inward_record")
public class InwardRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inward_id")
    private Integer inwardId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserMaster user;

    @Column(name = "serial_no", length = 100, nullable = false)
    private String serialNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_type_id")
    private DeviceType deviceType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id")
    private DeviceModel deviceModel;

    @Column(name = "custom_model_name")
    private String customModelName;

    @Column(name = "inward_remarks", columnDefinition = "TEXT")
    private String inwardRemarks;

    @Column(name = "created_by_employee_id")
    private Integer createdByEmployeeId;

    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;
}
