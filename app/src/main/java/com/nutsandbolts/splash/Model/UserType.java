package com.nutsandbolts.splash.Model;

/**
 * Enum to describe the 4 types of users.
 */
public enum UserType {
    /**
     * Code inspection will show these as unused declarations, but they are necessary for the
     * spinners to work properly.
     */
    CONTRIBUTOR ("Contributor"),
    WORKER ("Worker"),
    MANAGER ("Manager"),
    ADMINISTRATOR ("Administrator");

    private final String name;

    /**
     * Make a new UserType enum
     *
     * @param name  The user's usertype
     */
    UserType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}