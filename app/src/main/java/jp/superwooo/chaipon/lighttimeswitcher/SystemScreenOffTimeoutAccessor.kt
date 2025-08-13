package jp.superwooo.chaipon.lighttimeswitcher

import android.content.Context
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import jp.superwooo.chaipon.lighttimeswitcher.LimitTime

class SystemScreenOffTimeoutAccessor private constructor(private val mContext: Context) {
    fun read(): TimeDurationValue {
        val cr = mContext.contentResolver
        try {
            val timeOut = Settings.System.getInt(cr, Settings.System.SCREEN_OFF_TIMEOUT) / 1000
            return TimeDurationValue(timeOut, SettingsActivity.Companion.LimitTime)
        } catch (e: SettingNotFoundException) {
            e.printStackTrace()
            return TimeDurationValue(15, SettingsActivity.Companion.LimitTime)
        }
    }

    fun write(timeout: TimeDurationValue?) {
        Settings.System.putInt(
            mContext.contentResolver,
            Settings.System.SCREEN_OFF_TIMEOUT,
            timeout!!.milliSecond()
        )
    }

    companion object {
        fun create(context: Context): SystemScreenOffTimeoutAccessor {
            return SystemScreenOffTimeoutAccessor(context)
        }
    }
}
