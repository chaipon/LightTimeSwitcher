package jp.superwooo.chaipon.lighttimeswitcher.screen_timeout

import android.content.Context
import android.util.Log
import androidx.preference.PreferenceManager
import jp.superwooo.chaipon.lighttimeswitcher.R
import jp.superwooo.chaipon.lighttimeswitcher.ui.MainActivity
import jp.superwooo.chaipon.lighttimeswitcher.ui.SettingsActivity

class TimeDurationPreference(private val context: Context) {
    private val shortLongTimes: ShortLongTimes

    init {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val shortSec = preferences.getInt(MINIMUM_KEY, MainActivity.MIN_TIME / 1000)
        val longSec = preferences.getInt(MAXIMUM_KEY, MainActivity.MAX_TIME / 1000)
        shortLongTimes = ShortLongTimes(shortSec, longSec, SettingsActivity.LimitTime)
    }

    val short: TimeDurationValue
        get() = shortLongTimes.shortDuration
    val long: TimeDurationValue
        get() = shortLongTimes.longDuration

    fun getDurationValue(type: DurationType): TimeDurationValue {
        return when (type) {
            DurationType.Long -> shortLongTimes.longDuration
            DurationType.Short -> shortLongTimes.shortDuration
        }
    }

    fun getType(value: TimeDurationValue?): DurationType {
        return if (value == shortLongTimes.longDuration) DurationType.Long
        else DurationType.Short
    }

    fun save(shortLongTimes: ShortLongTimes) : String {
        val preferences = PreferenceManager.getDefaultSharedPreferences(
            context
        )
        val e = preferences.edit()
        val minimum = shortLongTimes.shortDuration.sec()
        val maximum = shortLongTimes.longDuration.sec()
        e.putInt(MINIMUM_KEY, minimum)
        e.putInt(MAXIMUM_KEY, maximum)
        e.apply()
        Log.d("LS", "save minimum: $minimum")
        Log.d("LS", "save maximum: $maximum")
        return context.getString(R.string.set_mini_max, minimum, maximum)
    }

    companion object {
        private const val MINIMUM_KEY = "minimumTime"
        private const val MAXIMUM_KEY = "maximumTime"
    }
}