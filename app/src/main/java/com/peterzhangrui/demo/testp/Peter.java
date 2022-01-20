package com.peterzhangrui.demo.testp;

import android.content.Context;
import android.widget.Toast;

import com.peterzhangrui.demo.coder.API;

public class Peter extends API<Void> {
    @Override
    public Void test(Context context) {
        Toast.makeText(context, "ddddd", Toast.LENGTH_LONG).show();
        return null;
    }
}
