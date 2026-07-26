package jp.superwooo.chaipon.lighttimeswitcher.schedule

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import jp.superwooo.chaipon.lighttimeswitcher.screen_timeout.DurationType
import jp.superwooo.chaipon.lighttimeswitcher.screen_timeout.TimeoutReceiver
import jp.superwooo.chaipon.lighttimeswitcher.ui.SettingsActivity
import java.time.Duration
import java.time.LocalTime

object AlarmScheduler {
    const val DURATION_TYPE_KEY: String = "duration_type"

    @SuppressLint("ScheduleExactAlarm")
    @JvmStatic
    fun scheduleTimeout(context: Context, type: DurationType, scheduleTime: LocalTime) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val scheduleElapsedTime = getElapsedTriggerTime(scheduleTime)
        Log.d("LS", "schedule to" + scheduleTime.toString() + "[" + scheduleElapsedTime + "]. type: " + type.name)
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            scheduleElapsedTime,
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
        var elapsedTime = SystemClock.elapsedRealtime()
        Log.d("LS", "After: " + delayTime + "ms")
        Log.d("LS", "Current elapsed time: " + elapsedTime + "ms")
        return elapsedTime + delayTime
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

    private fun buildPendingIntent(context: Context, type: DurationType): PendingIntent {
        val intent = Intent(context, TimeoutReceiver::class.java)
        intent.putExtra(DURATION_TYPE_KEY, type.name)
        return PendingIntent.getBroadcast(
            context,
            type.ordinal,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}