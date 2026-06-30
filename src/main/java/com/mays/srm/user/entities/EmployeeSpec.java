package com.mays.srm.user.entities;
import com.mays.srm.device.entities.DeviceType;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Employee_Spec")
@IdClass(EmployeeSpecId.class)
public class EmployeeSpec {

    @Id
    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Id
    @ManyToOne
    @JoinColumn(name = "device_id")
    private DeviceType deviceType;
}
