package jp.superwooo.chaipon.lighttimeswitcher;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

/**
 * Created by Minoru on 2016/11/06.
 */

public class NotificationController {
    private static final String CHANNEL_ID = "LightSwitcherNotification";
    Context mContext;
    TimeDurationValue mCurrentDurationValue;
    DurationType mDurationType;
    public NotificationController(Context context, DurationType durationType) {
        mContext = context;
        mDurationType = durationType;
        TimeDurationPreference timeDurationPreference = new TimeDurationPreference(context);
        mCurrentDurationValue = timeDurationPreference.getDurationValue(mDurationType);
    }
    private NotificationManager createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        NotificationManager notificationManager =
                (NotificationManager)mContext.getSystemService(mContext.NOTIFICATION_SERVICE);
        CharSequence name = "LS";
        String description = "LS";
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        channel.setShowBadge(false);
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        notificationManager.createNotificationChannel(channel);
        return notificationManager;
    }




    public void notifyTimeOut() {
        NotificationManager notificationManager = createNotificationChannel();
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(mContext, CHANNEL_ID);
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
        PendingIntent pending = PendingIntent.getActivity(mContext,
                0,
                new Intent(mContext, MainActivity.class),
                PendingIntent.FLAG_IMMUTABLE);
        notificationBuilder.setContentIntent(pending);
    }
    private void setNotificationForever(NotificationCompat.Builder notificationBuilder) {
        notificationBuilder.setOngoing(true);
    }

    private void setNotificationText(NotificationCompat.Builder notificationBuilder) {
        notificationBuilder.setContentTitle(mContext.getString(R.string.lighting_time));
        notificationBuilder.setContentText(getTimeoutMessage());
        notificationBuilder.setTicker(getTimeoutMessage());
    }
    private StringBuilder getTimeoutMessage(){
        return new StringBuilder(mContext.getString(R.string.setting_message, mCurrentDurationValue.sec()));
    }


    private void setNotificationIcon(NotificationCompat.Builder notificationBuilder) {
        if(mDurationType == DurationType.Short){
            Log.d("LS", "set icon short" );
            notificationBuilder.setSmallIcon(R.drawable.ic_stat_light_time_short);
        }else{
            Log.d("LS", "set icon long" );
            notificationBuilder.setSmallIcon(R.drawable.ic_stat_light_time_long);
        }
    }
}
