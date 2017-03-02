package com.nutsandbolts.splash.Model;


public enum UserType {
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

    /**
     * Returns a string representation of the user's usertype
     *
     * @return  The user's usertype
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}