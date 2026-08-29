package com.mays.srm.inventory.enums;

/**
 * Lifecycle of quotes.status.
 */
public enum QuoteStatus {
    DRAFT,
    SENT,
    ACCEPTED,
    REJECTED,
    EXPIRED;

    public static QuoteStatus from(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return QuoteStatus.valueOf(value.trim().toUpperCase());
    }
}
