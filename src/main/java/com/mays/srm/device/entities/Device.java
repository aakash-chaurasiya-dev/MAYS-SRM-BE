package com.mays.srm.device.entities;
import com.mays.srm.ticket.entities.Ticket;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "device")
public class Device {

    @Id
    @Column(name = "serial_no", length = 100)
    private String serialNo;

    @ManyToOne 
    @JoinColumn(name = "model_id")
    private DeviceModel model;

    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Ticket> tickets;

    @Column(name = "insert_date", insertable = false, updatable = false)
    private java.time.LocalDateTime insertDate;

    @Column(name = "last_update_date", insertable = false, updatable = false)
    private java.time.LocalDateTime lastUpdateDate;
}
