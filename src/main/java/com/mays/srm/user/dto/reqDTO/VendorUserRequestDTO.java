package com.mays.srm.user.dto.reqDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VendorUserRequestDTO {
    private Integer vendorId;
    private String user;
    private String contactNo;
    private Boolean isActive;
}
