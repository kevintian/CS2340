package com.nutsandbolts.splash.Model;

/**
 * Created by supra on 2/23/17.
 */

public enum WaterType {
    BOTTLED("Bottled"),
    WELL("Well"),
    STREAM("Stream"),
    LAKE("Lake"),
    SPRING("Spring"),
    OTHER("Other");

    private final String type;

    /**
     * Make a new WaterType enum
     *
     * @param name  The water's type
     */
    WaterType(String name) {
        this.type = name;
    }

    /**
     * Returns a string representation of the water's water type
     *
     * @return  The water's watertype
     */
    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return getType();
    }
}
