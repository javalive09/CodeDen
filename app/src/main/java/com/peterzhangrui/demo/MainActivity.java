package com.peterzhangrui.demo;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Button button = new Button(this);
        button.setText("framework api test");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tester.show(MainActivity.this);
            }
        });
        setContentView(button);
    }

}