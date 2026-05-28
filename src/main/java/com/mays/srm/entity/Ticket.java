package com.mays.srm.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Ticket")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private Integer ticketId;

    @ManyToOne
    @JoinColumn(name = "user_ref_no")
    private UserMaster userMaster;

    @ManyToOne
    @JoinColumn(name = "ticket_type")
    private TicketType ticketType;

    @ManyToOne
    @JoinColumn(name = "ticket_status")
    private Status ticketStatus;

    @Column(name = "email_id")
    private String emailId;

    @ManyToOne
    @JoinColumn(name = "device_id")
    private Device device;

    @Column(name = "ticket_description", columnDefinition = "TEXT")
    private String ticketDescription;

    @ManyToOne
    @JoinColumn(name = "ticket_branch")
    private Branch ticketBranch;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Column(name = "warranty_type")
    private String warrantyType;

    @Column(name = "priority", length = 20)
    private String priority;

    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(name = "last_updated_date")
    private LocalDateTime lastUpdatedDate;

    @Column(name = "mod_no")
    private Integer modNo = 0;
}
