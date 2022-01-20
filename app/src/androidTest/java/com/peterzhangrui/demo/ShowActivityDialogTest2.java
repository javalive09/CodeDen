package com.peterzhangrui.demo;

import static org.junit.Assert.assertEquals;

import android.app.Activity;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ShowActivityDialogTest2 {

    @Rule
    public ActivityTestRule<HelloWorldActivity> activityRule = new ActivityTestRule<>(HelloWorldActivity.class);

    @Test
    public void testOnCreate() throws Throwable {
        Activity activity = activityRule.getActivity();
//        activityRule.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
                assertEquals(new HelloWorld2().test(activity), new Integer(1));
//            }
//        });

    }

}