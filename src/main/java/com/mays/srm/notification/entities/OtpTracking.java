package com.mays.srm.notification.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "otp_tracking")
public class OtpTracking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "otp_id")
    private Integer otpId;

    @Column(name = "mobile_no", length = 15, nullable = false)
    private String mobileNo;

    @Column(name = "email_id", length = 100)
    private String emailId;

    @Column(name = "otp_hash", nullable = false)
    private String otpHash;

    @Column(name = "purpose", length = 20, nullable = false)
    private String purpose;

    @Column(name = "failed_attempts")
    @Builder.Default
    private Integer failedAttempts = 0;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "is_verified")
    @Builder.Default
    private Boolean isVerified = false;

    @Column(name = "created_at", updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
