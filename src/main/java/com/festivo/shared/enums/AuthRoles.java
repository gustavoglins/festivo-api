package com.festivo.shared.enums;

public enum AuthRoles {

    ADMIN("admin"),
    COMMON_USER("common_user");

    private String role;

    AuthRoles(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
