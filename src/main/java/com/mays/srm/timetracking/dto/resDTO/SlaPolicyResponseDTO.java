package com.mays.srm.timetracking.dto.resDTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SlaPolicyResponseDTO {
    private Long id;
    private Integer departmentId;
    private String departmentName;
    private String role;
    private Integer targetMinutes;
    private Boolean isTimerTracked;
    private Boolean isActive;
}
