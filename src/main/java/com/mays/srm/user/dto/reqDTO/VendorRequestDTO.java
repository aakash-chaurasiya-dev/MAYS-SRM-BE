package com.mays.srm.user.dto.reqDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VendorRequestDTO {
    private String name;
    private String description;
    private String address;
    private String email;
    private String mobileNo;
    private String password;          // plain text, will be encoded in service
    // private String roleName;          // e.g. ROLE_VENDOR
    private String referredBy;
    private Boolean isActive;

}
