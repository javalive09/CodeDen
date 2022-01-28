package com.peterzhangrui.demo;

import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

public class HelloWorldTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void hello() {
        new HelloWorld().hello(mActivityTestRule.getActivity());
    }
}