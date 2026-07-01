package com.mays.srm.ticket.entities;

import com.mays.srm.device.entities.DeviceAccessoryMaster;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ticket_accessories")
public class TicketAccessories {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_accessories_id")
    private Integer ticketAccessoriesId;

    @ManyToOne
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @ManyToOne
    @JoinColumn(name = "accessory_id", nullable = false)
    private DeviceAccessoryMaster accessory;

    @CreationTimestamp
    @Column(name = "insert_date", updatable = false)
    private Date insertDate;
}
