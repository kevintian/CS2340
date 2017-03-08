package com.nutsandbolts.splash.Model;

/**
 * Created by Suprabhat on 3/8/17.
 */

public enum WaterQuality {
    SAFE("Safe"),
    TREATABLE("Treatable"),
    UNSAFE("Unsafe");

    private final String quality;

    /**
     * Make a new WaterQuality enum
     *
     * @param quality  The water's quality
     */
    WaterQuality(String quality) {
        this.quality = quality;
    }

    /**
     * Returns a string representation of the water's condition
     *
     * @return  The water's condition
     */
    public String getQuality() {
        return quality;
    }

    @Override
    public String toString() {
        return getQuality();
    }
}
