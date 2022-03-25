package com.peterzhangrui.demo;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

public class Notification_ {

    @Tester.Test
    public void sendNotification(Activity activity) {
        // 构建 remoteView
        RemoteViews remoteView = new RemoteViews(activity.getPackageName(), R.layout.layout_notification);
        remoteView.setTextViewText(R.id.tvMsg, "RemoteViews............");
        remoteView.setImageViewResource(R.id.ivIcon, R.mipmap.ic_launcher_round);
        NotificationChannel channel = new NotificationChannel("123", "my_channel", NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights(true);
        channel.setLightColor(Color.GREEN);
        channel.setShowBadge(true);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(activity, "123");
        // 设置自定义 RemoteViews
        builder.setContent(remoteView).setSmallIcon(R.mipmap.ic_launcher);
        // 设置通知的优先级(悬浮通知)
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        // 设置通知的点击行为：这里启动一个 Activity
        Intent intent = new Intent(activity, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(activity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        Notification notification = builder.build();
        NotificationManager manager = (NotificationManager)activity.getSystemService(Context.NOTIFICATION_SERVICE);

        manager.createNotificationChannel(channel);
        manager.notify(1001, notification);
    }

    @Tester.Test
    public void sendNotificationExt(Activity activity) {
        // 构建 remoteView
        RemoteViews remoteView = new RemoteViews(activity.getPackageName(), R.layout.layout_notification);
        remoteView.setTextViewText(R.id.tvMsg, "RemoteViews............");
        remoteView.setImageViewResource(R.id.ivIcon, R.mipmap.ic_launcher_round);
        NotificationChannel channel = new NotificationChannel("123", "my_channel", NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights(true);
        channel.setLightColor(Color.GREEN);
        channel.setShowBadge(true);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(activity, "123");
        // 设置自定义 RemoteViews
        builder.setContent(remoteView).setSmallIcon(R.mipmap.ic_launcher);
        // 设置通知的优先级(悬浮通知)
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        // 设置通知的点击行为：这里启动一个 Activity
        Intent intent = new Intent(activity, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(activity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        Notification notification = builder.build();
        NotificationManager manager = (NotificationManager)activity.getSystemService(Context.NOTIFICATION_SERVICE);

        manager.createNotificationChannel(channel);
        manager.notify(1001, notification);
    }

}
