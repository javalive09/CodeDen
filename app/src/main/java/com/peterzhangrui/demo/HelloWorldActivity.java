package com.peterzhangrui.demo;

import android.app.Activity;
import com.peterzhangrui.demo.coder.APITester;

public class HelloWorldActivity extends APITester<Integer> {

    @Override
    public Integer test(Activity activity) {
        return 1;
    }

}
