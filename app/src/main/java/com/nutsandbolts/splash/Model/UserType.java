package com.nutsandbolts.splash.Model;


public enum UserType {
    CONTRIBUTOR ("Contributor"),
    WORKER ("Worker"),
    MANAGER ("Manager"),
    ADMINISTRATOR ("Administrator");

    private final String name;

    UserType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}