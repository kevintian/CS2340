package com.nutsandbolts.splash;

import com.nutsandbolts.splash.Model.WaterCondition;
import com.nutsandbolts.splash.Model.WaterSourceReport;
import com.nutsandbolts.splash.Model.WaterType;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;

/**
 * Class to test submission of water reports.
 */
public class IsValidLocationTest {
    private Date date;
    private long reportId;
    private String reporterName;
    private String reporterUID;
    private double latitude;
    private double longitude;
    private WaterType type;
    private WaterCondition condition;

    /**
     * Setup required variables to instantiate a WaterReport
     */
    @Before
    public void setup() {
        long curr = System.currentTimeMillis();
        date = new Date(curr);
        reportId = curr;
        reporterName = "George Burdell";
        reporterUID = "gburdell3";
        latitude = 0.0;
        longitude = 0.0;
        type = WaterType.LAKE;
        condition = WaterCondition.TREATABLE_CLEAR;
    }

    /**
     *  Helper method to instantiate a water report.
     *  @return a WaterSourceReport object with the current values of the member variables
     */
    public WaterSourceReport generateReport() {
        return new WaterSourceReport(date, reportId, reporterName, reporterUID, latitude, longitude,
                type, condition);
    }

    /**
     * Test to make sure invalid latitudes are not entered.
     */
    @Test
    public void latitudeOutOfRangeTest() {
        latitude = 1000.0;

        WaterSourceReport report = generateReport();
        assertEquals(false, report.isValidLocation());
    }

    /**
     * Test to make sure invalid longitudes are not entered.
     */
    @Test
    public void longitudeOutOfRangeTest() {
        longitude = 1000.0;

        WaterSourceReport report = generateReport();
        assertEquals(false, report.isValidLocation());
    }

    /**
     * Test to make sure both lat and long cannot be invalid together.
     */
    @Test
    public void bothOutOfRangeTest() {
        latitude = 1000.0;
        longitude = 1000.0;

        WaterSourceReport report = generateReport();
        assertEquals(false, report.isValidLocation());
    }

    /**
     * Test to make sure legal latitudes are valid
     */
    @Test
    public void latitudeInRangeTest() {
        latitude = 89.9;

        WaterSourceReport report = generateReport();
        assertEquals(true, report.isValidLocation());
    }

    /**
     * Test to make sure legal longitudes are valid
     */
    @Test
    public void longitudeInRangeTest() {
        longitude = 179.9;

        WaterSourceReport report = generateReport();
        assertEquals(true, report.isValidLocation());
    }

    /**
     * Test to make sure if both lat/long are legal the location is valid
     */
    @Test
    public void bothInRangeTest() {
        latitude = 89.9;
        longitude = 179.9;

        WaterSourceReport report = generateReport();
        assertEquals(true, report.isValidLocation());
    }

}
