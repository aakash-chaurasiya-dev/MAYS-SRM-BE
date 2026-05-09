package com.mays.srm.entity;

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
}
