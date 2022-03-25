package com.peterzhangrui.demo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;

public class Launch {

    @Tester.Test
    public void startSettings(Activity activity) {
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName("com.android.settings", "com.android.settings.Settings");
        intent.setComponent(componentName);
        activity.startActivity(intent);
    }

    @Tester.Test
    public void startLauncher(Activity activity) {
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName("com.did.launcherdemo", "com.did.launcherdemo.RouterActivity");
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(componentName);
        activity.startActivity(intent);
    }

    @Tester.Test
    public void moveTaskToBack(Activity activity) {
        activity.moveTaskToBack(true);
    }

}
