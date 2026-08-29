package com.mays.srm.inventory.enums;

/**
 * Lifecycle of a row in ticket_parts.part_status.
 * Set by the server from workflow actions — not from generic create/update DTOs.
 */
public enum TicketPartStatus {
    REQUESTED,
    APPROVED,
    REJECTED,
    QUOTED,
    ORDERED,
    PARTIAL,
    RECEIVED,
    CANCELLED;

    public static TicketPartStatus from(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return TicketPartStatus.valueOf(value.trim().toUpperCase());
    }
}
