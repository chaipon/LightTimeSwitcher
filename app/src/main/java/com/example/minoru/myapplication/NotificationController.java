package com.example.minoru.myapplication;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

/**
 * Created by Minoru on 2016/11/06.
 */

public class NotificationController {
    MainActivity mMainActivity;
    public NotificationController(MainActivity mainActivity) {
        mMainActivity = mainActivity;
    }

    public void notifyTimeOut() {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mMainActivity);

        setNotificationIcon(notificationBuilder);
        setNotificationText(notificationBuilder);

        setNotificationForever(notificationBuilder);

        setApplicationToPushNotification(notificationBuilder);

        NotificationManager notificationManager =
                (NotificationManager) mMainActivity.
                        getSystemService(mMainActivity.getApplicationContext().NOTIFICATION_SERVICE);
        notificationManager.notify(1, notificationBuilder.build());
    }
    private void setApplicationToPushNotification(NotificationCompat.Builder notificationBuilder) {
        PendingIntent pending = PendingIntent.getActivity(mMainActivity,
                0,
                new Intent(mMainActivity, MainActivity.class),
                0);
        notificationBuilder.setContentIntent(pending);
    }
    private void setNotificationForever(NotificationCompat.Builder notificationBuilder) {
        notificationBuilder.setOngoing(true);
    }

    private void setNotificationText(NotificationCompat.Builder notificationBuilder) {
        notificationBuilder.setContentTitle("点灯時間");
        notificationBuilder.setContentText(mMainActivity.getTimeoutMessage());
        notificationBuilder.setTicker(mMainActivity.getTimeoutMessage());
    }

    private void setNotificationIcon(NotificationCompat.Builder notificationBuilder) {
        if(mMainActivity.isMinimumTimeOut()){
            notificationBuilder.setSmallIcon(R.drawable.ic_stat_light_time_short);
        }else{
            notificationBuilder.setSmallIcon(R.drawable.ic_stat_light_time_long);
        }
    }
}
