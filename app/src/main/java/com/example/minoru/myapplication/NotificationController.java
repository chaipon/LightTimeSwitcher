package com.example.minoru.myapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

/**
 * Created by Minoru on 2016/11/06.
 */

@RequiresApi(api = Build.VERSION_CODES.M)
public class NotificationController {
    private static final String CHANNEL_ID = "LightSwitcherNotification";
    MainActivity mMainActivity;
    public NotificationController(MainActivity mainActivity) {
        mMainActivity = mainActivity;
    }
    private NotificationManager createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        NotificationManager notificationManager =
                (NotificationManager)mMainActivity.getSystemService(mMainActivity.getApplicationContext().NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "LS";
            String description = "LS";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.setShowBadge(false);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager.createNotificationChannel(channel);
        }
        return notificationManager;
    }




    public void notifyTimeOut() {
        NotificationManager notificationManager = createNotificationChannel();
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(mMainActivity, CHANNEL_ID);
        notificationBuilder.setCategory(NotificationCompat.CATEGORY_MESSAGE);
        setNotificationIcon(notificationBuilder);
        setNotificationText(notificationBuilder);


        setNotificationForever(notificationBuilder);

        setApplicationToPushNotification(notificationBuilder);

        Notification notification = notificationBuilder.build();

        notificationManager.notify(1, notification);
    }
    private void setApplicationToPushNotification(NotificationCompat.Builder notificationBuilder) {
        PendingIntent pending = PendingIntent.getActivity(mMainActivity,
                0,
                new Intent(mMainActivity, MainActivity.class),
                PendingIntent.FLAG_IMMUTABLE);
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
