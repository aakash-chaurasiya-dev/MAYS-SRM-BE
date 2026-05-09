package com.mays.srm.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonProperty;

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

    /*
    // This field lets the user ONLY pass the department ID in JSON
    @Transient // Not saved in DB directly, just used for JSON mapping
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // Only allowed on POST/PUT requests
    private Integer departmentId;
    */
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
    // Hide password from GET requests so it's not exposed to the public
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
}
