package com.nutsandbolts.splash;

/**
 * This test checks if the isWithInBounds method of WaterQualityReport is valid. All tests should
 * be passing at all times.
 *
 * @author Kevin Tian
 *
 */

import com.nutsandbolts.splash.Model.WaterQuality;
import com.nutsandbolts.splash.Model.WaterQualityReport;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Date;

public class ValidGraphPointTests {
    private static final int TIMEOUT = 200;
    private WaterQualityReport sampleReport;

    @Before
    public void setUp() {
        Date currentDate = new Date();
        long reportID = 1;
        String reporterName = "Kevin";
        String reporterUID = "TestUID";
        double latitude = 5.0;
        double longitude = 5.0;
        int virusPPM = 3;
        int contaminantPPM = 3;
        WaterQuality waterQuality = WaterQuality.SAFE;

        sampleReport = new WaterQualityReport(currentDate, reportID, reporterName, reporterUID,
                latitude, longitude, virusPPM, contaminantPPM, waterQuality);
    }

    @Test (timeout = TIMEOUT)
    public void testBasicValidReport() {
        int year = 2017;
        double latitude = 5.0;
        double longitude = 5.0;
        double radius = 20; //Radius is in miles
        assertTrue(sampleReport.isWithinBounds(year, latitude, longitude, radius));
    }

    @Test (timeout = TIMEOUT)
    public void testYearOutofBounds() {
        int year = 2016;
        double latitude = 5.0;
        double longitude = 5.0;
        double radius = 20; //Radius is in miles
        assertFalse(sampleReport.isWithinBounds(year, latitude, longitude, radius));
    }

    @Test (timeout = TIMEOUT)
    public void testNegativeYear() {
        int year = -1;
        double latitude = 5.0;
        double longitude = 5.0;
        double radius = 20; //Radius is in miles
        assertFalse(sampleReport.isWithinBounds(year, latitude, longitude, radius));
    }

    @Test (timeout = TIMEOUT)
    public void testZeroRadius() {
        int year = 2017;
        double latitude = 5.0;
        double longitude = 5.0;
        double radius = 0; //Radius is in miles

        //A radius of zero should not have anything in range, even if the coordinates are the same
        assertFalse(sampleReport.isWithinBounds(year, latitude, longitude, radius));
    }

    @Test (timeout = TIMEOUT)
    public void testNegativeRadius() {
        int year = 2017;
        double latitude = 5.0;
        double longitude = 5.0;
        double radius = -1; //Radius is in miles

        assertFalse(sampleReport.isWithinBounds(year, latitude, longitude, radius));
    }

    @Test (timeout = TIMEOUT)
    public void testOutOfRadius() {
        int year = 2017;
        double latitude = 5.0;
        double longitude = 4.5;
        double radius = 10; //Radius is in miles

        assertFalse(sampleReport.isWithinBounds(year, latitude, longitude, radius));
    }

    @Test (timeout = TIMEOUT)
    public void testChangingRadius() {
        int year = 2017;
        double latitude = 5.0;
        double longitude = 4.5;
        double radius = 10; //Radius is in miles

        assertFalse(sampleReport.isWithinBounds(year, latitude, longitude, radius));

        radius = 50;
        assertTrue(sampleReport.isWithinBounds(year, latitude, longitude, radius));
    }

    @Test (timeout = TIMEOUT)
    public void testNegativeLatitude() {
        int year = 2017;
        double latitude = -5.0;
        double longitude = 4.5;
        double radius = 1000; //Radius is in miles

        assertTrue(sampleReport.isWithinBounds(year, latitude, longitude, radius));
    }

    @Test (timeout = TIMEOUT)
    public void testNegativeLongitude() {
        int year = 2017;
        double latitude = 5.0;
        double longitude = -4.5;
        double radius = 1000; //Radius is in miles

        assertTrue(sampleReport.isWithinBounds(year, latitude, longitude, radius));
    }

    @Test (timeout = TIMEOUT)
    public void testOutOfBoundsLatitude() {
        int year = 2017;
        double latitude = 95.0;
        double longitude = 4.5;
        double radius = 1000; //Radius is in miles

        assertFalse(sampleReport.isWithinBounds(year, latitude, longitude, radius));

        latitude = -95.0;
        assertFalse(sampleReport.isWithinBounds(year, latitude, longitude, radius));
    }

    @Test (timeout = TIMEOUT)
    public void testOutOfBoundsLongitude() {
        int year = 2017;
        double latitude = 5.0;
        double longitude = 184.5;
        double radius = 1000; //Radius is in miles

        assertFalse(sampleReport.isWithinBounds(year, latitude, longitude, radius));

        longitude = -184.0;
        assertFalse(sampleReport.isWithinBounds(year, latitude, longitude, radius));
    }


}
