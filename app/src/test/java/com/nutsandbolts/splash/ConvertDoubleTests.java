package com.nutsandbolts.splash;

import com.nutsandbolts.splash.Model.WaterSourceReport;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Deb Banerji on 31-Mar-17.
 * <p>
 * Testing class for the convertDouble function
 */

public class ConvertDoubleTests {

    private static final int TIMEOUT = 200;

    private static final double EPSILON = 1e-15; // close to machine precision
    // used to compare floating point numbers

    /**
     * Test long with value 300
     */
    @Test(timeout = TIMEOUT)
    public void testBasicPositiveLong() {
        Long testValue = 300L;
        assertEquals(300.0, WaterSourceReport.convertDouble(testValue), EPSILON);
    }

    /**
     * Test long with value -300
     */
    @Test(timeout = TIMEOUT)
    public void testBasicNegativeLong() {
        Long testValue = -300L;
        assertEquals(-300.0, WaterSourceReport.convertDouble(testValue), EPSILON);
    }

    /**
     * Test long with value 0
     */
    @Test(timeout = TIMEOUT)
    public void testZeroLong() {
        Long testValue = 0L;
        assertEquals(0, WaterSourceReport.convertDouble(testValue), EPSILON);
    }

    /**
     * Test long with max value
     */
    @Test(timeout = TIMEOUT)
    public void testMaxLong() {
        Long testValue = Long.MAX_VALUE;
        assertEquals((double) Long.MAX_VALUE, WaterSourceReport.convertDouble(testValue), EPSILON);
    }

    /**
     * Test long with min value
     */
    @Test(timeout = TIMEOUT)
    public void testMinLong() {
        Long testValue = Long.MIN_VALUE;
        assertEquals((double) Long.MIN_VALUE, WaterSourceReport.convertDouble(testValue), EPSILON);
    }

    /**
     * Test double with value 300
     */
    @Test(timeout = TIMEOUT)
    public void testBasicPositiveDouble() {
        Double testValue = 300.0;
        assertEquals(300.0, WaterSourceReport.convertDouble(testValue), EPSILON);
    }

    /**
     * Test double with value -300
     */
    @Test(timeout = TIMEOUT)
    public void testBasicNegativeDouble() {
        Double testValue = -300.0;
        assertEquals(-300.0, WaterSourceReport.convertDouble(testValue), EPSILON);
    }

    /**
     * Test double with (non whole) value 300.5
     */
    @Test(timeout = TIMEOUT)
    public void testDecimalPositiveDouble() {
        Double testValue = 300.5;
        assertEquals(300.5, WaterSourceReport.convertDouble(testValue), EPSILON);
    }

    /**
     * Test double with (non whole) value -300.5
     */
    @Test(timeout = TIMEOUT)
    public void testDecimalNegativeDouble() {
        Double testValue = -300.5;
        assertEquals(-300.5, WaterSourceReport.convertDouble(testValue), EPSILON);
    }

    /**
     * Test double with value 0
     */
    @Test(timeout = TIMEOUT)
    public void testZeroDouble() {
        Double testValue = 0.0;
        assertEquals(0, WaterSourceReport.convertDouble(testValue), EPSILON);
    }

    /**
     * Test double with max value
     */
    @Test(timeout = TIMEOUT)
    public void testMaxDouble() {
        Double testValue = Double.MAX_VALUE;
        assertEquals(Double.MAX_VALUE, WaterSourceReport.convertDouble(testValue), EPSILON);
    }

    /**
     * Test double with min value
     */
    @Test(timeout = TIMEOUT)
    public void testMinDouble() {
        Double testValue = Double.MIN_VALUE;
        assertEquals(Double.MIN_VALUE, WaterSourceReport.convertDouble(testValue), EPSILON);
    }

    /**
     * Test Object that is neither long nor double
     */
    @Test(timeout = TIMEOUT, expected = IllegalArgumentException.class)
    public void testOtherObject() {
        Object other = new Object();
        assertEquals(0, WaterSourceReport.convertDouble(other), EPSILON);
    }
}
