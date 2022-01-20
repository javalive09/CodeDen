package com.peterzhangrui.demo;

import android.content.Context;

import com.peterzhangrui.demo.coder.API;

public class HelloWorld2 extends API<Integer> {
    @Override
    public Integer test(Context context) {
        logD("HelloWorld2", "test//////////////");
        return 1;
    }

}
