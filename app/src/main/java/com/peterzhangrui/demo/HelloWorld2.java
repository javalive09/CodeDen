package com.peterzhangrui.demo;

import android.content.Context;

import com.peterzhangrui.demo.coder.API;

public class HelloWorld2 implements API.Tester<Integer> {
    @Override
    public Integer test(Context context) {
        API.logD("HelloWorld2", "test//////////////");
        return 1;
    }

}
