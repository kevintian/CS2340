package com.nutsandbolts.splash.Model;

import java.util.Date;

/**
 * Created by Deb Banerji on 22-Feb-17.
 */

public class WaterReport {
    private Date dateTime;
    private int reportNumber;
    private String reporterName;
    private String reporterUID;
    private double latitude;
    private double longitude;

    //TODO: Add in Field and ENUM for type of water  (Bottled, Well, Stream, Lake, Spring, Other)
    //TODO: Add in Field and ENUM for condition of water (Waste, Treatable-Clear, Treatable-Muddy, Potable)
}