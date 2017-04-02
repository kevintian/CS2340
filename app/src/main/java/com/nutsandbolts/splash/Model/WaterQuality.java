package com.nutsandbolts.splash.Model;

/**
 * Enum to describe the 3 types of water quality.
 */
public enum WaterQuality {
    /**
     * Code inspection will show these as unused declarations, but they are necessary for the
     * spinners to work properly.
     */
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
