package com.mays.srm.user.entities;

import com.mays.srm.enquiry.entities.Enquiry;
import com.mays.srm.enquiry.entities.InwardRecord;
import com.mays.srm.enquiry.entities.OutwardRecord;
import com.mays.srm.ticket.entities.Ticket;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_entry_report")
public class UserEntryReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "entry_no")
    private Integer entryNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserMaster user;

    @Column(name = "reason", columnDefinition = "TEXT", nullable = false)
    private String reason;

    @Column(name = "entry_type", length = 30)
    private String entryType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enquiry_id")
    private Enquiry enquiry;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inward_id")
    private InwardRecord inward;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outward_id")
    private OutwardRecord outward;

    @CreationTimestamp
    @Column(name = "entry_date", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date entryDate;
}

