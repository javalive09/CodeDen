package com.peterzhangrui.demo;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.hardware.display.DisplayManager;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ShowActivityDialogTest {

    @Test
    public void testOnCreate() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        DisplayManager displayManager = (DisplayManager) appContext.getSystemService(Context.DISPLAY_SERVICE);
        displayManager.getDisplay(0);
        assertEquals(new HelloWorld2().test(appContext), new Integer(1));
    }

}