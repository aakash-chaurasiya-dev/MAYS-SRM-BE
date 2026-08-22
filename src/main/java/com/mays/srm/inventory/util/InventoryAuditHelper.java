package com.mays.srm.inventory.util;

import com.mays.srm.timetracking.util.StatusAccessValidator;

/**
 * Inventory audit fields store Employee.employee_id
 * (staff login maps CustomUserDetails.userId → employeeId).
 */
public final class InventoryAuditHelper {

    private InventoryAuditHelper() {
    }

    /** Current logged-in employee id, or null for portal users. */
    public static Integer currentEmployeeId() {
        return StatusAccessValidator.getCurrentEmployeeId();
    }

    /** @deprecated use {@link #currentEmployeeId()} */
    @Deprecated
    public static Integer currentUserId() {
        return currentEmployeeId();
    }
}
