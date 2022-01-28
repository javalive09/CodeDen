package com.peterzhangrui.demo;

import android.content.Context;

import com.peterzhangrui.demo.coder.API;
import com.peterzhangrui.demo.coder.Tester;

public class HelloWorld3{

    @Tester.Test
    public Integer test(Context context) {
        Tester.logD("HelloWorld3", "test//////////////");
        return 1;
    }

}
