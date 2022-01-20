package com.peterzhangrui.demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.View;
import android.widget.Button;

import com.peterzhangrui.demo.coder.Coder;

public class ShowSystemDialog extends Coder.AutoCreator {
    @Override
    public void onCreate(Activity activity) {
        Button button = new Button(activity);
        button.setText("click to show system dialog");
        activity.setContentView(button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(activity).setTitle("dialog").create();
                alertDialog.getWindow().setType(2038);
                alertDialog.show();
            }
        });
    }

}
