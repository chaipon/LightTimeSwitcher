package jp.superwooo.chaipon.lighttimeswitcher.screen_timeout

import android.content.Context
import android.provider.Settings
import jp.superwooo.chaipon.lighttimeswitcher.ui.SettingsActivity

class SystemScreenOffTimeoutAccessor private constructor(private val context: Context) {
    fun read(): TimeDurationValue {
        val cr = context.contentResolver
        try {
            val timeOut = Settings.System.getInt(cr, Settings.System.SCREEN_OFF_TIMEOUT) / 1000
            return TimeDurationValue(timeOut, SettingsActivity.LimitTime)
        } catch (e: Settings.SettingNotFoundException) {
            e.printStackTrace()
            return TimeDurationValue(15, SettingsActivity.LimitTime)
        }
    }

    fun write(timeout: TimeDurationValue?) {
        Settings.System.putInt(
            context.contentResolver,
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