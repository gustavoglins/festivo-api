package com.festivo.shared.enums;

public enum PartyStatus {

    DRAFT("draft"),
    PUBLISHED("published"),
    CANCELLED("cancelled"),
    COMPLETED("completed"),
    ONGOING("ongoing");

    private String status;

    PartyStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
