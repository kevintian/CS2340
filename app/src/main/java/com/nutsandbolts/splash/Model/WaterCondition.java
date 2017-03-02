package com.nutsandbolts.splash.Model;

/**
 * Created by supra on 2/23/17.
 */

public enum WaterCondition {
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