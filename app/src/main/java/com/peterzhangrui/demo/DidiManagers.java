package com.peterzhangrui.demo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.Window;

import androidx.annotation.NonNull;

import com.didi.DidiConstants;
import com.didi.DidiManager;

import java.lang.reflect.Method;

public class DidiManagers {

    @Tester.Test
    public void registerWindowInfoReceiver(Activity context) {
        DidiManager didiManager = (DidiManager) context.getSystemService(DidiConstants.Base.KEY_SERVICE);
        final Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                Log.i("peter", "msg = " + msg);
                switch (msg.what) {
                    case DidiConstants.WindowInfoService.ACTION_SHOW:

                        break;
                    default:
                        break;
                }
            }
        };

        Method[] methods = didiManager.getClass().getDeclaredMethods();
        for (int len = methods.length, i = 0; i < len; i++) {
            Log.i("DidiManagers", "methodName=" + methods[0].getName());
        }

        didiManager.register(DidiConstants.WindowInfoService.ID, "peter-test", new Messenger(handler));
    }

    @Tester.Test
    public void unregisterWindowInfoReceiver(Activity context) {
        DidiManager didiManager = (DidiManager) context.getSystemService(DidiConstants.Base.KEY_SERVICE);
        didiManager.unregister(DidiConstants.WindowInfoService.ID, "peter-test");
    }

    @Tester.Test
    public void sendShowWindowInfo(Activity context) {
        DidiManager didiManager = (DidiManager) context.getSystemService(DidiConstants.Base.KEY_SERVICE);
        didiManager.call(DidiConstants.WindowInfoService.ID, DidiConstants.WindowInfoService.ACTION_SHOW, null);
    }

    @Tester.Test
    public void sendHideWindowInfo(Activity context) {
        DidiManager didiManager = (DidiManager) context.getSystemService(DidiConstants.Base.KEY_SERVICE);
        didiManager.call(DidiConstants.WindowInfoService.ID, DidiConstants.WindowInfoService.ACTION_HIDE, null);
    }

    @Tester.Test
    public void getWindowInfo(Context context) {
        DidiManager didiManager = (DidiManager) context.getSystemService(DidiConstants.Base.KEY_SERVICE);
        Bundle bundle = didiManager.get(DidiConstants.WindowInfoService.ID, DidiConstants.WindowInfoService.ACTION_HIDE, null);
        Log.i("peter", bundle.toString());
    }

}
