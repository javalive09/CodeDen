package com.peterzhangrui.demo;

import android.app.Activity;
import android.app.AlertDialog;

public class Dialog {

    @Tester.Test
    public void showSystemDialog(Activity activity) {
        AlertDialog alertDialog = new AlertDialog.Builder(activity).setTitle("dialog").create();
        alertDialog.getWindow().setType(2038);
        alertDialog.show();
    }

    @Tester.Test
    public void showActivityDialog(Activity activity) {
        new AlertDialog.Builder(activity).setTitle("dialog").show();
    }

}
