package com.nutsandbolts.splash.Model;

/**
 * Enum to describe the types of water.
 */
public enum WaterType {
    /**
     * Code inspection will show these as unused declarations, but they are necessary for the
     * spinners to work properly.
     */
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
     * @return  The water's type
     */
    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return getType();
    }
}
