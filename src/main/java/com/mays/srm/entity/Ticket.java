package com.mays.srm.entity;

import jakarta.persistence.*;
import lombok.*;

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
}
