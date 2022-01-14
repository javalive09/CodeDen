package com.peterzhangrui.demo;

import android.app.Activity;
import android.widget.ImageView;

import com.peterzhangrui.demo.coder.Coder;

public class HelloWorld extends Coder.AutoCreator {
    @Override
    public void onCreate(Activity activity) {
        ImageView imageView = new ImageView(activity);
        imageView.setImageResource(R.drawable.ic_launcher_foreground);
        activity.setContentView(imageView);
    }

}
