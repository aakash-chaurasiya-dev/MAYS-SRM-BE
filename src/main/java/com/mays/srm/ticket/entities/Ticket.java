package com.mays.srm.ticket.entities;
import com.mays.srm.user.entities.Employee;
import com.mays.srm.device.entities.Device;
import com.mays.srm.user.entities.UserMaster;
import com.mays.srm.organization.entities.Status;
import com.mays.srm.organization.entities.Branch;
import com.mays.srm.user.entities.Vendor;
import com.mays.srm.user.entities.VendorUser;
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

//    @Column(name = "email_id")
//    private String emailId;

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

    @ManyToOne
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;

    @ManyToOne
    @JoinColumn(name = "vendor_user_id")
    private VendorUser vendorUser;

    @ManyToOne
    @JoinColumn(name = "parent_ticket_id")
    private Ticket parentTicket;

    @ManyToOne
    @JoinColumn(name = "referred_category_id")
    private ReferredCategory referredCategory;

    @Column(name = "referred_category_decription_ticket", columnDefinition = "TEXT")
    private String referredCategoryDecriptionTicket;

    @ManyToOne
    @JoinColumn(name = "warranty_type_id")
    private WarrantyType warrantyType;

    @Column(name = "priority", length = 20)
    private String priority;

    @Column(name = "target_date")
    private LocalDateTime targetDate;

    @Column(name = "closed_date")
    private LocalDateTime closedDate;

    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(name = "last_updated_date")
    private LocalDateTime lastUpdatedDate;

    @Column(name = "mod_no")
    private Integer modNo = 0;

    public Integer getTicketId() {
        return this.ticketId;
    }

    public void setTicketId(Integer ticketId) {
        this.ticketId = ticketId;
    }
}
