package com.mays.srm.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set; // Use Set for collections in JPA

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private Integer employeeId;

    @Column(name = "employee_name")
    private String employeeName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(name = "vendor")
    private String vendor;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "pincode")
    private String pincode;

    @Column(name = "city")
    private String city;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "mobile_no", unique = true)
    private String mobileNo;

    @Column(name = "role")
    private String role;

    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "password")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    // Establish the one-to-many relationship with EmployeeSpec
    // When an Employee is deleted, all associated EmployeeSpec records will also be deleted.
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EmployeeSpec> employeeSpecs;
}
