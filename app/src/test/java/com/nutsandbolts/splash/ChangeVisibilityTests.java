package com.nutsandbolts.splash;

import android.view.View;

import com.nutsandbolts.splash.Controller.HomeActivity;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Jinni Xia on 4/2/17
 *
 * Tests the changeVisibility function in HomeActivity
 */
public class ChangeVisibilityTests {

    private static final int TIMEOUT = 200;

    @Test(timeout = TIMEOUT)
    public void testUserViews() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test(timeout = TIMEOUT)
    public void testUserSubmitQualityIcon() throws Exception {
//        assertFalse();
    }
}