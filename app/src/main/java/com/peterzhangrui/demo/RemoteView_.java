package com.peterzhangrui.demo;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.widget.RemoteViews;

public class RemoteView_ {

    @Tester.Test
    public void show(Activity activity) {
        RemoteViews remoteViews = new RemoteViews(activity.getPackageName(), R.layout.layout_notification);
        remoteViews.setTextViewText(R.id.tvMsg, "RemoteViews............");
        remoteViews.setImageViewResource(R.id.ivIcon, R.mipmap.ic_launcher_round);
        Intent intent = new Intent("com.peterzhangrui.demo");
        intent.setPackage("com.did.remoteviews");
        intent.putExtra("peter", remoteViews);
        activity.sendBroadcast(intent);
    }

    @Tester.Test
    public void updateText(Activity activity) {
        RemoteViews remoteViews = new RemoteViews(activity.getPackageName(), R.layout.layout_notification);
        remoteViews.setTextViewText(R.id.tvMsg, "RemoteViews............update");
        remoteViews.setImageViewResource(R.id.ivIcon, R.mipmap.ic_launcher_round);
        Intent intent = new Intent("com.peterzhangrui.demo");
        intent.setPackage("com.did.remoteviews");
        intent.putExtra("peter", remoteViews);
        activity.sendBroadcast(intent);
    }
    @Tester.Test
    public void pendingIntent(Activity activity) {
        RemoteViews remoteViews = new RemoteViews(activity.getPackageName(), R.layout.layout_notification);
        remoteViews.setTextViewText(R.id.tvMsg, "RemoteViews............pendingIntent");
        remoteViews.setImageViewResource(R.id.ivIcon, R.mipmap.ic_launcher_round);

        PendingIntent pendingIntent = PendingIntent.getActivity(activity, 12345,
                new Intent(activity, activity.getClass()), PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.ivIcon, pendingIntent);

        Intent intent = new Intent("com.peterzhangrui.demo");
        intent.setPackage("com.did.remoteviews");
        intent.putExtra("peter", remoteViews);
        activity.sendBroadcast(intent);
    }

}
