package com.mays.srm.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "User_Master")
public class UserMaster {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String userId;

    @Column(name = "mobile_no", unique = true)
    private String mobileNo;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email_id", unique = true)
    private String emailId;
    
    @Column(name = "password")
    private String password;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @Column(name = "role")
    private String role = "ROLE_USER";

    @Column(name = "is_active")
    private Boolean isActive = true;

 /*   @PrePersist
    @PreUpdate
    protected void syncMobileNo() {
        if (this.userId != null) {
            this.mobileNo = this.userId;
        } else if (this.mobileNo != null) {
            this.userId = this.mobileNo;
        }
    }*/
}
