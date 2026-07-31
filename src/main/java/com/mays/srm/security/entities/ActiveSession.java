package com.mays.srm.security.entities;
import com.mays.srm.user.entities.Employee;
import com.mays.srm.user.entities.UserMaster;
import com.mays.srm.user.entities.Vendor;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "active_sessions")
public class ActiveSession {

    @Id
    @Column(name = "session_id", length = 36)
    private String sessionId; // UUID JTI

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserMaster user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Column(name = "device_info")
    private String deviceInfo;

    @Column(name = "login_ip", length = 45)
    private String loginIp;

    @Column(name = "login_time", updatable = false)
    @Builder.Default
    private LocalDateTime loginTime = LocalDateTime.now();

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
}