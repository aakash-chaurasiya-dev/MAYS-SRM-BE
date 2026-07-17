package com.mays.srm.security.entities;
import com.mays.srm.user.entities.Employee;
import com.mays.srm.user.entities.UserMaster;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "security_profile")
public class SecurityProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Integer profileId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserMaster user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Column(name = "last_pass_update")
    private LocalDateTime lastPassUpdate;

    @Column(name = "token_version")
    @Builder.Default
    private Integer tokenVersion = 1;

    @Column(name = "first_time_login")
    @Builder.Default
    private Boolean firstTimeLogin = false;

    @Column(name = "no_of_failed_attempts")
    @Builder.Default
    private Integer noOfFailedAttempts = 0;

    @Column(name = "account_locked_until")
    private LocalDateTime accountLockedUntil;

    @CreationTimestamp
    @Column(name = "insert_date", updatable = false)
    private LocalDateTime insertDate;

    @UpdateTimestamp
    @Column(name = "last_update_date")
    private LocalDateTime lastUpdateDate;
}