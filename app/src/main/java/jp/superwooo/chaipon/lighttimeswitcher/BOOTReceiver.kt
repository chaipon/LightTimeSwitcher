package jp.superwooo.chaipon.lighttimeswitcher

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import jp.superwooo.chaipon.lighttimeswitcher.AlarmScheduler.scheduleAll

class BOOTReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        Log.d("LS", "BOOTReceiver triggered with action: $action")
        if (Intent.ACTION_BOOT_COMPLETED == action) {
            try {
                if (SchedulePreference.Companion.create(context).isEnabled) scheduleAll(context)
            } catch (e: SecurityException) {
                Log.e("LS", "scheduling error because of security error")
            }
        } else {
            Log.d("LS", "BOOTReceiver ignored non-boot action: $action")
        }
    }
}
