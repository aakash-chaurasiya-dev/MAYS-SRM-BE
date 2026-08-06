package com.mays.srm.timetracking.dto.request;

import lombok.Data;

@Data
public class SlaPolicyRequestDTO {
    private Integer departmentId;
    private String role;
    private Integer targetMinutes;
    private Boolean isTimerTracked;
    private Boolean isActive;
}
