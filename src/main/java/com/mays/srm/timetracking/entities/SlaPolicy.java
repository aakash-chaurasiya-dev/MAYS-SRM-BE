package com.mays.srm.timetracking.entities;

import com.mays.srm.organization.entities.Department;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sla_policy")
@Data
@NoArgsConstructor
public class SlaPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(name = "role")
    private String role;

    @Column(name = "target_minutes", nullable = false)
    private Integer targetMinutes;

    @Column(name = "is_timer_tracked", nullable = false)
    private Boolean isTimerTracked = true;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
