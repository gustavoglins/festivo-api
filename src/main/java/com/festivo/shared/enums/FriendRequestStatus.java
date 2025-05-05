package com.festivo.shared.enums;

public enum FriendRequestStatus {

    PENDING("pending"),
    ACCEPTED("accepted"),
    REJECTED("rejected");

    private String status;

    FriendRequestStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
