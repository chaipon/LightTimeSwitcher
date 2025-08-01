package jp.superwooo.chaipon.lighttimeswitcher;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Calendar;

public class AlarmScheduler {
    static final String DurationTypeKey = "duration_type";
    public static void scheduleTimeout(Context context, DurationType type, LocalTime scheduleTime) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Log.d("LS", "schedule to" + scheduleTime.toString() + ". type: " + type.name());
        alarmManager.setExact(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                getElapsedTriggerTime(scheduleTime),
                buildPendingIntent(context, type)
        );
    }
    private static long getElapsedTriggerTime(LocalTime scheduleTime){
        LocalTime now = LocalTime.now();
        long delayTime = Duration.between(now, scheduleTime).toMillis();
        if(delayTime < 0) {
            Log.i("LS", "Invalid delay time: " + delayTime);
            delayTime += Duration.ofHours(24).toMillis();
        }
        Log.d("LS", "After: " + delayTime + "ms");
        return SystemClock.elapsedRealtime() + delayTime;
    }
    public static void cancel(Context context, DurationType type) {
        Log.d("LS", "cancel: " + type.name());
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(buildPendingIntent(context, type));
    }
    public static void scheduleAll(Context context){
        EnableTimePreference enableShortTimePreference = EnableTimePreference.create(context, SettingsActivity.EnableTimeShortKeyPref);
        EnableTimePreference enableLongTimePreference = EnableTimePreference.create(context, SettingsActivity.EnableTimeLongKeyPref);
        Log.d("LS", "schedule all setting");

        if(enableLongTimePreference.isEnabled())
            scheduleTimeout(context, DurationType.Long, enableLongTimePreference.loadTime());

        if(enableShortTimePreference.isEnabled())
            scheduleTimeout(context, DurationType.Short, enableShortTimePreference.loadTime());

    }
    public static void cancelAll(Context context){
        EnableTimePreference enableShortTimePreference = EnableTimePreference.create(context, SettingsActivity.EnableTimeShortKeyPref);
        EnableTimePreference enableLongTimePreference = EnableTimePreference.create(context, SettingsActivity.EnableTimeLongKeyPref);
        Log.d("LS", "cancel all schedule");

        if(enableLongTimePreference.isEnabled())
            cancel(context, DurationType.Long);

        if(enableShortTimePreference.isEnabled())
            cancel(context, DurationType.Short);

    }
    public static PendingIntent buildPendingIntent(Context context, DurationType type){
        Intent intent = new Intent(context, TimeoutReceiver.class);
        intent.putExtra(DurationTypeKey, type.name());
        return PendingIntent.getBroadcast(
                context, type.ordinal(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }
}
