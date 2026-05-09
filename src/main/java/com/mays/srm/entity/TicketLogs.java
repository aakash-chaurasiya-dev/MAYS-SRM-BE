package com.mays.srm.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Ticket_Logs")
public class TicketLogs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Integer logId;

    @ManyToOne
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @ManyToOne
    @JoinColumn(name = "assignor_employee")
    private Employee assignorEmployee;

    @ManyToOne
    @JoinColumn(name = "assignee_employee")
    private Employee assigneeEmployee;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @Column(name = "column_name")
    private String columnName;

    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;

    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue;

    @Column(name = "assignor_remarks", columnDefinition = "TEXT")
    private String assignorRemarks;
}
