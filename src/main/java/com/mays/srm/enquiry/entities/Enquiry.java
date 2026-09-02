package com.mays.srm.enquiry.entities;
import com.mays.srm.device.entities.Brand;
import com.mays.srm.device.entities.DeviceModel;
import com.mays.srm.device.entities.DeviceType;
import com.mays.srm.ticket.entities.Ticket;
import com.mays.srm.user.entities.UserMaster;
import com.mays.srm.organization.entities.Status;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Enquiry")
public class Enquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enquiry_id")
    private Integer enquiryId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserMaster user;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @Column(name = "serial_no")
    private String serialNo;

    @ManyToOne
    @JoinColumn(name = "device_type_id")
    private DeviceType deviceType;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @ManyToOne
    @JoinColumn(name = "model_id")
    private DeviceModel deviceModel;

    @Column(name = "custom_model_name")
    private String customModelName;

    @Column(name = "enquiry_for")
    private String enquiryFor;

    @Column(name = "query_text", columnDefinition = "TEXT")
    private String queryText;

    @Column(name = "remark", columnDefinition = "TEXT")
    private String remark;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private Status status;

    @ManyToOne
    @JoinColumn(name = "converted_ticket_id")
    private Ticket convertedTicket;

    @Column(name = "is_converted")
    private Boolean isConverted = false;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "mobile_no")
    private String mobileNo;

    @Column(name = "email_id")
    private String emailId;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;
}

