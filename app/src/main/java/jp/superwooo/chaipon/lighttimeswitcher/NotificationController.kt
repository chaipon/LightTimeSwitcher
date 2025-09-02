package jp.superwooo.chaipon.lighttimeswitcher

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat


/**
 * Created by Minoru on 2016/11/06.
 */
class NotificationController(private val context: Context, private val durationType: DurationType) {
    private val currentDurationValue: TimeDurationValue?

    init {
        val timeDurationPreference = TimeDurationPreference(context)
        currentDurationValue = timeDurationPreference.getDurationValue(durationType)
    }

    private fun createNotificationChannel(): NotificationManager {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val name: CharSequence = "LS"
        val description = "LS"
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(CHANNEL_ID, name, importance)
        channel.description = description
        channel.setShowBadge(false)
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        notificationManager.createNotificationChannel(channel)
        return notificationManager
    }


    fun notifyTimeOut() {
        val notificationManager = createNotificationChannel()
        val notificationBuilder =
            NotificationCompat.Builder(context, CHANNEL_ID)
        notificationBuilder.setCategory(NotificationCompat.CATEGORY_MESSAGE)
        setNotificationIcon(notificationBuilder)
        setNotificationText(notificationBuilder)


        setNotificationForever(notificationBuilder)

        setApplicationToPushNotification(notificationBuilder)

        val notification = notificationBuilder.build()

        Log.d("LS", "notify")
        notificationManager.notify(1, notification)
    }

    private fun setApplicationToPushNotification(notificationBuilder: NotificationCompat.Builder) {
        val pending = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        notificationBuilder.setContentIntent(pending)
    }

    private fun setNotificationForever(notificationBuilder: NotificationCompat.Builder) {
        notificationBuilder.setOngoing(true)
    }

    private fun setNotificationText(notificationBuilder: NotificationCompat.Builder) {
        notificationBuilder.setContentTitle(context.getString(R.string.lighting_time))
        notificationBuilder.setContentText(timeoutMessage)
        notificationBuilder.setTicker(timeoutMessage)
    }

    private val timeoutMessage: StringBuilder
        get() = StringBuilder(
            context.getString(
                R.string.setting_message,
                currentDurationValue!!.sec()
            )
        )


    private fun setNotificationIcon(notificationBuilder: NotificationCompat.Builder) {
        if (durationType === DurationType.Short) {
            Log.d("LS", "set icon short")
            notificationBuilder.setSmallIcon(R.drawable.ic_stat_light_time_short)
        } else {
            Log.d("LS", "set icon long")
            notificationBuilder.setSmallIcon(R.drawable.ic_stat_light_time_long)
        }
    }

    companion object {
        private const val CHANNEL_ID = "LightSwitcherNotification"
    }
}
