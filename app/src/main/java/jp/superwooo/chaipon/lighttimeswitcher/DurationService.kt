package jp.superwooo.chaipon.lighttimeswitcher

import android.content.Context
import android.util.Log

class DurationService(private val context: Context, durationType: DurationType) {
    private var durationType = DurationType.Short

    init {
        this.durationType = durationType
    }

    fun setTimeOut() {
        val currentDuration: TimeDurationValue =
            SystemScreenOffTimeoutAccessor.Companion.create(context).read()
        val preference = TimeDurationPreference(context)
        val settingDuration = preference.getDurationValue(durationType)
        if (currentDuration == settingDuration) return
        Log.d("LS", "set time out: " + settingDuration!!.sec())
        SystemScreenOffTimeoutAccessor.create(context).write(settingDuration)

        val notificationController =
            NotificationController(context, durationType)
        notificationController.notifyTimeOut()
    }
}
