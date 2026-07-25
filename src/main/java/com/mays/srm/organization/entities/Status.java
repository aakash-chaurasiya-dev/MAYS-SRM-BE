package com.mays.srm.organization.entities;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Status")
public class Status {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "status_id")
    private Integer statusId;

    @Column(name = "status_name")
    private String statusName;

    @Column(name = "status_flg")
    private Integer statusFlg;

    @Column(name = "status_description")
    private String statusDescription;

    @Column(name = "status_type")
    private String statusType;

    @jakarta.persistence.Column(name = "is_locked")
    private Boolean isLocked = false;

    @Column(name = "allowed_department_ids")
    private String allowedDepartmentIds;

    @Column(name = "allowed_roles")
    private String allowedRoles;

    @org.hibernate.annotations.CreationTimestamp
    @jakarta.persistence.Column(name = "insert_date", updatable = false)
    private java.util.Date insertDate;

    @org.hibernate.annotations.UpdateTimestamp
    @jakarta.persistence.Column(name = "last_update_date")
    private java.util.Date lastUpdateDate;
}
