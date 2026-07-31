package com.mays.srm.user.dto.resDTO;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class VendorResponseDTO {
    private Integer id;
    private String name;
    private String description;
    private String address;
    private String email;
    private String mobileNo;
    private String roleName;
    private Boolean isActive;
    private LocalDateTime insertedAt;
    private LocalDateTime updatedAt;
    private String referredBy;
}
