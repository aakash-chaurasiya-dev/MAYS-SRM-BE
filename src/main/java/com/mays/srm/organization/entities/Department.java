package com.mays.srm.organization.entities;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Department")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "department_id")
    private Integer departmentId;

    @Column(name = "department_name")
    private String departmentName;

    @Column(name = "department_description")
    private String departmentDescription;

    @jakarta.persistence.Column(name = "is_locked")
    private Boolean isLocked = false;

    @org.hibernate.annotations.CreationTimestamp
    @jakarta.persistence.Column(name = "insert_date", updatable = false)
    private java.util.Date insertDate;

    @org.hibernate.annotations.UpdateTimestamp
    @jakarta.persistence.Column(name = "last_update_date")
    private java.util.Date lastUpdateDate;

    public Integer getDepartmentId() {
        return this.departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return this.departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getDepartmentDescription() {
        return this.departmentDescription;
    }

    public void setDepartmentDescription(String departmentDescription) {
        this.departmentDescription = departmentDescription;
    }
}
