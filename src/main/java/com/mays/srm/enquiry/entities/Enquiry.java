package com.mays.srm.enquiry.entities;
import com.mays.srm.device.entities.Brand;
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
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @Column(name = "enquiry_for")
    private String enquiryFor;

    @Column(name = "query_text", columnDefinition = "TEXT")
    private String queryText;

    @Column(name = "remark", columnDefinition = "TEXT")
    private String remark;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private Status status;
}
