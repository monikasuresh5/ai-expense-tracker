package com.monika.expensetrackerbackend.model;

public enum Category {
    FOOD,
    TRAVEL,
    BILLS,
    SHOPPING,
    ENTERTAINMENT,
    HEALTH,
    EDUCATION,
    OTHER;

    public static boolean isValid(String value) {
        for (Category c : values()) {
            if (c.name().equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }
}