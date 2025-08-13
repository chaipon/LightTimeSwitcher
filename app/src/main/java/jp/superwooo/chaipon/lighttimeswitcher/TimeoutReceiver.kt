package jp.superwooo.chaipon.lighttimeswitcher

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import jp.superwooo.chaipon.lighttimeswitcher.AlarmScheduler.scheduleAll

class TimeoutReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val durationTypeName = intent.getStringExtra(AlarmScheduler.DurationTypeKey)
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
        scheduleAll(context.applicationContext)
    }
}
