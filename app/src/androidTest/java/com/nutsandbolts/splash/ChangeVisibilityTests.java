package com.nutsandbolts.splash;

import android.support.test.rule.ActivityTestRule;
import com.nutsandbolts.splash.Controller.HomeActivity;

import junit.framework.AssertionFailedError;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

/**
 * Created by Jinni Xia on 4/2/17
 *
 * Tests the changeVisibility function in HomeActivity
 */
public class ChangeVisibilityTests {

    private static final int TIMEOUT = 5000;

    @Rule
    public ActivityTestRule<HomeActivity> mActivityRule = new ActivityTestRule(HomeActivity.class);

//    @Test(expected = AssertionFailedError.class)
//    public void checkContributerVsibilities() {
//        //this should pass when you're a user and the submit quality report icon doesn't appear
//        onView(withId(R.id.submit_quality_report_icon)).check(matches(isDisplayed()));
//        fail();
//    }

    /**
     * You should pass this test if you are logged in as a contributor or admin and the submit
     * quality report icon/text are not visible, view quality reports icon/text are not visible, and
     * view graph icon/text are not visible
     * If any of these are visible, then the test fails
     */
    @Test
    public void checkContributorAdminVisibilities() {
        try {
            //if the icon is displayed, then an exception is not thrown and therefore fails
            onView(withId(R.id.submit_quality_report_icon)).check(matches(isDisplayed()));
            fail();
        } catch(AssertionFailedError e) {
            //comes into this block if an exception is thrown (meaning icon is not displayed)
            //and then moves onto the next try catch block, checking to make sure the other
            //icons/texts are not displayed
        }
        try {
            onView(withId(R.id.submit_quality_report_text)).check(matches(isDisplayed()));
            fail();
        } catch(AssertionFailedError e) {

        }
        try {
            onView(withId(R.id.view_quality_report_icon)).check(matches(isDisplayed()));
            fail();
        } catch(AssertionFailedError e) {

        }
        try {
            onView(withId(R.id.view_quality_reports_text)).check(matches(isDisplayed()));
            fail();
        } catch(AssertionFailedError e) {

        }
        try {
            onView(withId(R.id.view_graph_icon)).check(matches(isDisplayed()));
            fail();
        } catch(AssertionFailedError e) {

        }
        try {
            onView(withId(R.id.view_graph_text)).check(matches(isDisplayed()));
            fail();
        } catch(AssertionFailedError e) {

        }
    }

    /**
     * You should pass this test if you are logged in as a worker and the submit quality report
     * icon/text are visible, view quality reports icon/text are not visible, and
     * view graph icon/text are not visible
     */
    @Test
    public void checkWorkerVisibilities() {
        try {
            //if the icon is displayed, then an exception is not thrown and therefore fails
            onView(withId(R.id.view_quality_report_icon)).check(matches(isDisplayed()));
            fail();
        } catch(AssertionFailedError e) {
            //comes into this block if an exception is thrown (meaning icon is not displayed)
            //and then moves onto the next try catch block, checking to make sure the other
            //icons/texts are not displayed
        }
        try {
            onView(withId(R.id.view_quality_reports_text)).check(matches(isDisplayed()));
            fail();
        } catch(AssertionFailedError e) {

        }
        try {
            onView(withId(R.id.view_graph_icon)).check(matches(isDisplayed()));
            fail();
        } catch(AssertionFailedError e) {

        }
        try {
            onView(withId(R.id.view_graph_text)).check(matches(isDisplayed()));
            fail();
        } catch(AssertionFailedError e) {

        }
        try {
            //if the icon is displayed, then this is successful and doesn't need to go to catch
            onView(withId(R.id.submit_quality_report_icon)).check(matches(isDisplayed()));
        } catch(AssertionFailedError e) {
            //comes into this block if there is an exception that the icon is not displayed, this
            //makes you fail the test
            fail();
        }
        try {
            onView(withId(R.id.submit_quality_report_text)).check(matches(isDisplayed()));
        } catch(AssertionFailedError e) {
            fail();
        }
    }

    /**
     * You should pass this test if you are logged in as a manager and the submit quality report
     * icon/text are visible, view quality reports icon/text are visible, and
     * view graph icon/text are visible
     * If any of these are invisible, then the test fails
     */
    @Test
    public void checkManagerVisibilities() {
        try {
            //if the icon is displayed, then this is successful and doesn't need to go to catch
            onView(withId(R.id.submit_quality_report_icon)).check(matches(isDisplayed()));
        } catch(AssertionFailedError e) {
            //comes into this block if there is an exception that the icon is not displayed, this
            //makes you fail the test
            fail();
        }
        try {
            onView(withId(R.id.submit_quality_report_text)).check(matches(isDisplayed()));
        } catch(AssertionFailedError e) {
            fail();
        }
        try {
            onView(withId(R.id.view_quality_report_icon)).check(matches(isDisplayed()));
        } catch(AssertionFailedError e) {
            fail();
        }
        try {
            onView(withId(R.id.view_quality_reports_text)).check(matches(isDisplayed()));
        } catch(AssertionFailedError e) {
            fail();
        }
        try {
            onView(withId(R.id.view_graph_icon)).check(matches(isDisplayed()));
        } catch(AssertionFailedError e) {
            fail();
        }
        try {
            onView(withId(R.id.view_graph_text)).check(matches(isDisplayed()));
        } catch(AssertionFailedError e) {
            fail();
        }
    }

    /**
     * You should pass this test no matter what you are logged in as because these icons/text views
     * should be visible to any kind of user
     */
    @Test
    public void checkUniversalVisibilities() {
        try {
            //if the icon is displayed, then this is successful and doesn't need to go to catch
            onView(withId(R.id.sign_out_icon)).check(matches(isDisplayed()));
        } catch(AssertionFailedError e) {
            //comes into this block if there is an exception that the icon is not displayed, this
            //makes you fail the test
            fail();
        }
        try {
            onView(withId(R.id.edit_profile_icon)).check(matches(isDisplayed()));
        } catch(AssertionFailedError e) {
            fail();
        }
        try {
            onView(withId(R.id.submit_water_report_icon)).check(matches(isDisplayed()));
        } catch(AssertionFailedError e) {
            fail();
        }
        try {
            onView(withId(R.id.view_water_reports_icon)).check(matches(isDisplayed()));
        } catch(AssertionFailedError e) {
            fail();
        }
        try {
            onView(withId(R.id.view_map_icon)).check(matches(isDisplayed()));
        } catch(AssertionFailedError e) {
            fail();
        }
    }

}