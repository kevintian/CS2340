package com.nutsandbolts.splash.Model;

/**
 * Enum to describe the 4 water conditions.
 */
public enum WaterCondition {
    /**
     * Code inspection will show these as unused declarations, but they are necessary for the
     * spinners to work properly.
     */
    WASTE("Waste"),
    TREATABLE_CLEAR("Treatable-Clear"),
    TREATABLE_MUDDY("Treatable-Muddy"),
    POTABLE("Potable");

    private final String condition;

    /**
     * Make a new WaterCondition enum
     *
     * @param condition  The water's condition
     */
    WaterCondition(String condition) {
        this.condition = condition;
    }

    /**
     * Returns a string representation of the water's condition
     *
     * @return  The water's condition
     */
    public String getCondition() {
        return condition;
    }

    @Override
    public String toString() {
        return getCondition();
    }
}