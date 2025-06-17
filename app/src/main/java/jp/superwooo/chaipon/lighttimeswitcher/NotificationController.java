package jp.superwooo.chaipon.lighttimeswitcher;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

/**
 * Created by Minoru on 2016/11/06.
 */

@RequiresApi(api = Build.VERSION_CODES.M)
public class NotificationController {
    private static final String CHANNEL_ID = "LightSwitcherNotification";
    MainActivity _mainActivity;
    public NotificationController(MainActivity mainActivity) {
        _mainActivity = mainActivity;
    }
    private NotificationManager createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        NotificationManager notificationManager =
                (NotificationManager) _mainActivity.getSystemService(_mainActivity.getApplicationContext().NOTIFICATION_SERVICE);
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
                new NotificationCompat.Builder(_mainActivity, CHANNEL_ID);
        notificationBuilder.setCategory(NotificationCompat.CATEGORY_MESSAGE);
        setNotificationIcon(notificationBuilder);
        setNotificationText(notificationBuilder);


        setNotificationForever(notificationBuilder);

        setApplicationToPushNotification(notificationBuilder);

        Notification notification = notificationBuilder.build();

        Log.d("LS", "notify" );
        notificationManager.notify(1, notification);
    }
    private void setApplicationToPushNotification(NotificationCompat.Builder notificationBuilder) {
        PendingIntent pending = PendingIntent.getActivity(_mainActivity,
                0,
                new Intent(_mainActivity, MainActivity.class),
                PendingIntent.FLAG_IMMUTABLE);
        notificationBuilder.setContentIntent(pending);
    }
    private void setNotificationForever(NotificationCompat.Builder notificationBuilder) {
        notificationBuilder.setOngoing(true);
    }

    private void setNotificationText(NotificationCompat.Builder notificationBuilder) {
        notificationBuilder.setContentTitle(_mainActivity.getString(R.string.lighting_time));
        notificationBuilder.setContentText(_mainActivity.getTimeoutMessage());
        notificationBuilder.setTicker(_mainActivity.getTimeoutMessage());
    }

    private void setNotificationIcon(NotificationCompat.Builder notificationBuilder) {
        if(_mainActivity.isShortDurationTime()){
            Log.d("LS", "set icon short" );
            notificationBuilder.setSmallIcon(R.drawable.ic_stat_light_time_short);
        }else{
            Log.d("LS", "set icon long" );
            notificationBuilder.setSmallIcon(R.drawable.ic_stat_light_time_long);
        }
    }
}
