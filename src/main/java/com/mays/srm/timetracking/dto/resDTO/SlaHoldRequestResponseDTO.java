package com.mays.srm.timetracking.dto.resDTO;

import java.time.LocalDateTime;

import com.mays.srm.timetracking.enums.HoldRequestStatus;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SlaHoldRequestResponseDTO {
    private Long id;
    private Integer ticketId;
    private Long trackingId;
    private Integer requestedById;
    private String requestedByName;
    private String reason;
    private HoldRequestStatus status;
    private LocalDateTime requestedAt;
    private Integer reviewedById;
    private String reviewedByName;
    private LocalDateTime reviewedAt;
    private String reviewRemark;
    private LocalDateTime releasedAt;
    private String ticketStatusName;
    private String assigneeName;
}
