package com.mays.srm.timetracking.service;

import com.mays.srm.user.entities.Employee;

public interface SlaCalculationService {

    int resolveTargetMinutes(Employee assignee);

    boolean isTimerTracked(Employee assignee);
}
