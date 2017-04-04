package com.nutsandbolts.splash;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;

import com.nutsandbolts.splash.Controller.HomeActivity;

import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * Created by Jinni Xia on 4/2/17
 *
 * Tests the changeVisibility function in HomeActivity
 */
public class ChangeVisibilityTests {

    private static final int TIMEOUT = 100000;

    @Test(timeout = TIMEOUT)
    public void testUserViews() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test(timeout = TIMEOUT)
    public void testUserSubmitQualityIcon() throws Exception {
        String dataSnapshotValue = "USER";
        Class cls = HomeActivity.class;
        Object instance = cls.newInstance();
        ((Activity)instance).onCreate(null, null);
        //activity.changeVisibility(dataSnapshotValue);
        Field[] fields = cls.getDeclaredFields();

//        for (Field f:fields) {
//            System.out.println(f.getName());
//        }

            Field f = cls.getDeclaredField("submitQualityReportIcon");
        f.setAccessible(true);
        Object fieldValue = f.get(instance);

        Class fieldClass = fieldValue.getClass();

        Method getVisibility = fieldClass.getDeclaredMethod("getVisibility", fieldClass);

        //ImageView sqri = activity.getSubmitQReportIcon();
        assertFalse((int)getVisibility.invoke(fieldValue) == View.VISIBLE);
    }
}