package com.mays.srm.inventory.enums;

public enum PartSource {
    VENDOR,
    MARKET,
    STOCK_IN,
    STOCK_OUT;

    public static PartSource from(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return PartSource.valueOf(value.trim().toUpperCase());
    }
}
