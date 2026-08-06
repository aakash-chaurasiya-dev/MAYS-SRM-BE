package com.mays.srm.timetracking.enums;

public enum SlaTimerAction {
    NONE,
    CREATE_HOLD_REQUEST,
    PAUSE_TIMER,
    RESUME_TIMER,
    STOP_TIMER;

    public static SlaTimerAction fromString(String value) {
        if (value == null || value.isBlank()) {
            return NONE;
        }
        try {
            return SlaTimerAction.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return NONE;
        }
    }
}
