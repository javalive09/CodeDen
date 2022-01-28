package com.peterzhangrui.demo;

import android.app.Activity;

public class HelloWorld {

    @Tester.Test
    public Integer hello(Activity activity) {
        Tester.logD("HelloWorld3", "test//////////////");
        return 1;
    }

}
