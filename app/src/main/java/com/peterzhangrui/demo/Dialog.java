package com.peterzhangrui.demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class Dialog {

    @Tester.Test
    public void showSystemDialog(Activity activity) {
        AlertDialog alertDialog = new AlertDialog.Builder(activity).setTitle("dialog").create();
        alertDialog.getWindow().setType(2038);
        alertDialog.show();
    }

    @Tester.Test
    public void showActivityDialog(Activity activity) {
        AlertDialog dialog = new AlertDialog.Builder(activity).setTitle("dialog").create();
        dialog.show();
        dialog.getWindow().getDecorView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.i("peter", "event = " + event);
                return false;
            }
        });

    }


}
