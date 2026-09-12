package com.mays.srm.enquiry.enums;

/**
 * Lifecycle status of an Enquiry.
 * Set by the server from workflow actions.
 */
public enum EnquiryStatus {
    QUERIED,
    RESPONDED,
    INWARDED,
    TICKET_CREATED,
    HANDED_OFF;

    public static EnquiryStatus from(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return EnquiryStatus.valueOf(value.trim().toUpperCase());
    }
}
