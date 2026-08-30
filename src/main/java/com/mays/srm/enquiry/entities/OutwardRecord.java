package com.mays.srm.enquiry.entities;

import com.mays.srm.ticket.entities.Ticket;
import com.mays.srm.user.entities.UserMaster;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "outward_record")
public class OutwardRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "outward_id")
    private Integer outwardId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserMaster user;

    @Column(name = "serial_no", length = 100, nullable = false)
    private String serialNo;

    @Column(name = "outward_status", length = 50)
    private String outwardStatus = "COMPLETED";

    @Column(name = "outward_remarks", columnDefinition = "TEXT")
    private String outwardRemarks;

    @Column(name = "handover_to_name")
    private String handoverToName;

    @Column(name = "handover_to_phone", length = 20)
    private String handoverToPhone;

    @Column(name = "created_by_employee_id")
    private Integer createdByEmployeeId;

    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;
}
