package com.mays.srm.ticket.entities;

import java.time.LocalDateTime;

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
import com.mays.srm.user.entities.Employee;

@Entity
@Table(name = "ticket_time_tracking")
@Data
@NoArgsConstructor
public class TicketTimeTracking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id", nullable = false)
    private Employee assignee;

    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt;

    @Column(name = "released_at")
    private LocalDateTime releasedAt;

    @Column(name = "accumulated_minutes", nullable = false)
    private Integer accumulatedMinutes = 0;

    @Column(name = "last_clock_start")
    private LocalDateTime lastClockStart;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
