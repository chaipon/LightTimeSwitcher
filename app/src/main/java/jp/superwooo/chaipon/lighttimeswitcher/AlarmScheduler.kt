package jp.superwooo.chaipon.lighttimeswitcher

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import java.time.Duration
import java.time.LocalTime

object AlarmScheduler {
    const val DurationTypeKey: String = "duration_type"
    @SuppressLint("ScheduleExactAlarm")
    @JvmStatic
    fun scheduleTimeout(context: Context, type: DurationType, scheduleTime: LocalTime) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        Log.d("LS", "schedule to" + scheduleTime.toString() + ". type: " + type.name)
        alarmManager.setExact(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            getElapsedTriggerTime(scheduleTime),
            buildPendingIntent(context, type)
        )
    }

    private fun getElapsedTriggerTime(scheduleTime: LocalTime): Long {
        val now = LocalTime.now()
        var delayTime = Duration.between(now, scheduleTime).toMillis()
        if (delayTime < 0) {
            Log.i("LS", "Invalid delay time: $delayTime")
            delayTime += Duration.ofHours(24).toMillis()
        }
        Log.d("LS", "After: " + delayTime + "ms")
        return SystemClock.elapsedRealtime() + delayTime
    }

    @JvmStatic
    fun cancel(context: Context, type: DurationType) {
        Log.d("LS", "cancel: " + type.name)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(buildPendingIntent(context, type))
    }

    @JvmStatic
    fun scheduleAll(context: Context) {
        val enableShortTimePreference =
            EnableTimePreference.create(context, SettingsActivity.EnableTimeShortKeyPref)
        val enableLongTimePreference =
            EnableTimePreference.create(context, SettingsActivity.EnableTimeLongKeyPref)
        Log.d("LS", "schedule all setting")

        if (enableLongTimePreference.isEnabled) scheduleTimeout(
            context,
            DurationType.Long,
            enableLongTimePreference.loadTime()
        )

        if (enableShortTimePreference.isEnabled) scheduleTimeout(
            context,
            DurationType.Short,
            enableShortTimePreference.loadTime()
        )
    }

    @JvmStatic
    fun cancelAll(context: Context) {
        val enableShortTimePreference =
            EnableTimePreference.create(context, SettingsActivity.EnableTimeShortKeyPref)
        val enableLongTimePreference =
            EnableTimePreference.create(context, SettingsActivity.EnableTimeLongKeyPref)
        Log.d("LS", "cancel all schedule")

        if (enableLongTimePreference.isEnabled) cancel(context, DurationType.Long)

        if (enableShortTimePreference.isEnabled) cancel(context, DurationType.Short)
    }

    fun buildPendingIntent(context: Context?, type: DurationType): PendingIntent {
        val intent = Intent(context, TimeoutReceiver::class.java)
        intent.putExtra(DurationTypeKey, type.name)
        return PendingIntent.getBroadcast(
            context,
            type.ordinal,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
