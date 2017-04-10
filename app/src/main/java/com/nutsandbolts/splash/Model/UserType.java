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

    private final String userType;

    /**
     * Make a new UserType enum
     *
     * @param userType  The user's user type
     */
    UserType(String userType) {
        this.userType = userType;
    }

    @Override
    public String toString() {
        return userType;
    }
}