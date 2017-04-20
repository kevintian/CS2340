package com.nutsandbolts.splash.Model;

/**
 * Enum to specify the types of security log events.
 */
public enum SecurityLogType {
    LOGIN("Login"), CREATE_REPORT("Create Report");

    String type;

    SecurityLogType(String name) {
        this.type = name;
    }

    public String getType() {
        return type;
    }
}
