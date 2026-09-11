package com.mays.srm.enquiry.entities;

import com.mays.srm.device.entities.DeviceModel;
import com.mays.srm.enquiry.enums.EnquiryStatus;
import com.mays.srm.ticket.entities.Ticket;
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
@Entity
@Table(name = "enquiry")
public class Enquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enquiry_id")
    private Integer enquiryId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserMaster user;

    @Column(name = "enquiry_for")
    private String enquiryFor;

    @Column(name = "query_text", columnDefinition = "TEXT")
    private String queryText;

    @Column(name = "remark", columnDefinition = "TEXT")
    private String remark;

    // ✅ OLD: @ManyToOne private Status status; (DELINK kar diya)
    // ✅ NEW: Direct Enum as String in DB
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)  // DB mein naya column 'status' (VARCHAR)
    private EnquiryStatus status;

    @CreationTimestamp
    @Column(name = "insert_date", updatable = false)
    private LocalDateTime insertDate;

    @UpdateTimestamp
    @Column(name = "last_update_date")
    private LocalDateTime lastUpdateDate;

    @Column(name = "is_converted")
    private Boolean isConverted;

    @ManyToOne
    @JoinColumn(name = "converted_ticket_id")
    private Ticket convertedTicket;

    @Column(name = "serial_no", nullable = false, length = 100)
    private String serialNo;

    @ManyToOne
    @JoinColumn(name = "model_id")
    private DeviceModel deviceModel;

    @Column(name = "action", nullable = false)
    private Integer action;
}