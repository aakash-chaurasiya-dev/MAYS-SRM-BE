package com.mays.srm.inventory.enums;

/**
 * Lifecycle of parts_order.status.
 * Recomputed on save from parts_master.received counts; final RECEIVED may use PATCH.
 */
public enum PartsOrderStatus {
    ORDERED,
    PARTIAL,
    RECEIVED,
    CANCELLED;

    public static PartsOrderStatus from(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return PartsOrderStatus.valueOf(value.trim().toUpperCase());
    }
}
