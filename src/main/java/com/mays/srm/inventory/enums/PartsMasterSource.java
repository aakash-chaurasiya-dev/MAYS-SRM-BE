package com.mays.srm.inventory.enums;

/**
 * Source of a unit in parts_master (and in_stock_part where applicable).
 */
public enum PartsMasterSource {
    VENDOR,
    MARKET,
    STOCK;

    public static PartsMasterSource from(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return PartsMasterSource.valueOf(value.trim().toUpperCase());
    }
}
