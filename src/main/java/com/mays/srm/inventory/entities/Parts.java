package com.mays.srm.inventory.entities;
import com.mays.srm.inventory.enums.PartSource;
import com.mays.srm.ticket.entities.Ticket;
import com.mays.srm.organization.entities.Status;
import com.mays.srm.user.entities.Vendor;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Parts")
public class Parts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "part_id")
    private Integer partId;

    @ManyToOne
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @Column(name = "part_name")
    private String partName;

    @Column(name = "quantity")
    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Inventory product;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", length = 20)
    private PartSource source;

    @ManyToOne
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;

    @Column(name = "unit_cost")
    private BigDecimal unitCost;

    @Column(name = "defective_returned")
    private Boolean defectiveReturned = false;

    @Column(name = "customer_approved")
    private Boolean customerApproved;

    @CreationTimestamp
    @Column(name = "order_date", updatable = false)
    private LocalDateTime orderDate;

    @Column(name = "receive_date")
    private LocalDateTime receiveDate;

    @Column(name = "used_date")
    private LocalDateTime usedDate;

    @Column(name = "return_date")
    private LocalDateTime returnDate;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    /** True once STOCK_IN receive or STOCK_OUT consume has been applied to inventory. */
    @Column(name = "stock_applied")
    private Boolean stockApplied = false;
}
