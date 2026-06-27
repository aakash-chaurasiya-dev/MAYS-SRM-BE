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

    @Column(name = "modification_date")
    private LocalDateTime modificationDate;

    @Column(name = "assignor_remarks", columnDefinition = "TEXT")
    private String assignorRemarks;

    @ManyToOne
    @JoinColumn(name = "old_status")
    private Status oldStatus;

    @ManyToOne
    @JoinColumn(name = "new_status")
    private Status newStatus;

    @Column(name = "changed_fields", columnDefinition = "json")
    private String changedFields;

    @ManyToOne
    @JoinColumn(name = "modified_by")
    private Employee modifiedBy;
}
