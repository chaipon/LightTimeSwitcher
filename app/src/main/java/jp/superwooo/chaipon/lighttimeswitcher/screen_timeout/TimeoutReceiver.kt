package jp.superwooo.chaipon.lighttimeswitcher.screen_timeout

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import jp.superwooo.chaipon.lighttimeswitcher.schedule.AlarmScheduler

class TimeoutReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val durationTypeName = intent.getStringExtra(AlarmScheduler.DURATION_TYPE_KEY)
        Log.d("LS", "Time out receive: $durationTypeName")
        var durationService: DurationService?
        try {
            durationService =
                DurationType.valueOf(durationTypeName!!).create(context.applicationContext)
        } catch (e: Exception) {
            Log.e("LS", "duration type get error$e")
            durationService =
                DurationType.Short.create(context.applicationContext)
        }
        durationService!!.setTimeOut()
        AlarmScheduler.scheduleAll(context.applicationContext)
    }
}