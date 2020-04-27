package com.hcsp.api;

public enum DataStatus {
    OK(),
    DELETED(),

    // Only for order
    PENDING(),
    PAID(),
    DELIVERED(),
    RECEIVED();

    public String getName() {
        return name().toLowerCase();
    }
}
