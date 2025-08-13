package jp.superwooo.chaipon.lighttimeswitcher

import android.content.Context
import android.util.Log

class DurationService(private val mContext: Context, durationType: DurationType) {
    private var mDurationType = DurationType.Short

    init {
        mDurationType = durationType
    }

    fun setTimeOut() {
        val currentDuration: TimeDurationValue =
            SystemScreenOffTimeoutAccessor.Companion.create(mContext).read()
        val preference = TimeDurationPreference(mContext)
        val settingDuration = preference.getDurationValue(mDurationType)
        if (currentDuration == settingDuration) return
        Log.d("LS", "set time out: " + settingDuration!!.sec())
        SystemScreenOffTimeoutAccessor.Companion.create(mContext).write(settingDuration)

        val notificationController =
            NotificationController(mContext, mDurationType)
        notificationController.notifyTimeOut()
    }
}
