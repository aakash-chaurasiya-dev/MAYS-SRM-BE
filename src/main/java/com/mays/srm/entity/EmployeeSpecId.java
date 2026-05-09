package com.mays.srm.entity;

import lombok.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeSpecId implements Serializable {

    private Integer employee;
    private Integer deviceType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeeSpecId that = (EmployeeSpecId) o;
        return employee.equals(that.employee) && deviceType.equals(that.deviceType);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(employee, deviceType);
    }
}
