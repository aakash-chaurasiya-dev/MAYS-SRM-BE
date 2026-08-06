package com.mays.srm.timetracking.dto.resDTO;

import java.time.LocalDateTime;

import com.mays.srm.timetracking.enums.HoldRequestStatus;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TicketTimeTrackingResponseDTO {
    private Long id;
    private Integer ticketId;
    private Integer assigneeId;
    private String assigneeName;
    private LocalDateTime assignedAt;
    private LocalDateTime releasedAt;
    private Integer accumulatedMinutes;
    private LocalDateTime lastClockStart;
    private Boolean isActive;
    private Integer targetMinutes;
    private Boolean isCrossedTAT;
    private Boolean isTimerPaused;
}
