package com.mays.srm.user.dto.reqDTO;

import lombok.*;

@Getter
@Setter
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
}
