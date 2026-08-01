package com.mays.srm.user.dto.resDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VendorUserResponseDTO {
    private Integer id;
    private Integer vendorId;
    private String vendorName;
    private String user;
    private String contactNo;
    private Boolean isActive;
    private LocalDateTime insertedAt;
    private LocalDateTime updatedAt;
}
