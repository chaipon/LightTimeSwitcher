package jp.superwooo.chaipon.lighttimeswitcher

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.preference.PreferenceManager
import jp.superwooo.chaipon.lighttimeswitcher.LimitTime

class TimeDurationPreference(private val mContext: Context?) {
    private val shortLongTimes: ShortLongTimes

    init {
        val preferences = PreferenceManager.getDefaultSharedPreferences(
            mContext!!
        )
        val shortSec = preferences.getInt(MinimumKey, MainActivity.Companion.MinTime / 1000)
        val longSec = preferences.getInt(MaximumKey, MainActivity.Companion.MaxTime / 1000)
        shortLongTimes = ShortLongTimes(shortSec, longSec, SettingsActivity.Companion.LimitTime)
    }

    val short: TimeDurationValue
        get() = shortLongTimes.shortDuration
    val long: TimeDurationValue
        get() = shortLongTimes.longDuration

    fun getDurationValue(type: DurationType?): TimeDurationValue? {
        return when (type) {
            DurationType.Long -> shortLongTimes.longDuration
            DurationType.Short -> shortLongTimes.shortDuration
            else -> throw IllegalArgumentException("Unexpected type:$type")
        }
    }

    fun getType(value: TimeDurationValue?): DurationType {
        return if (value == shortLongTimes.longDuration) DurationType.Long
        else DurationType.Short
    }

    fun save(shortLongTimes: ShortLongTimes) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(
            mContext!!
        )
        val e = preferences.edit()
        val minimum = shortLongTimes.shortDuration.sec()
        val maximum = shortLongTimes.longDuration.sec()
        e.putInt(MinimumKey, minimum)
        e.putInt(MaximumKey, maximum)
        e.apply()
        Log.d("LS", "save minimum: $minimum")
        Log.d("LS", "save maximum: $maximum")
        val message = mContext.getString(R.string.set_mini_max, minimum, maximum)
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val MinimumKey = "minimumTime"
        private const val MaximumKey = "maximumTime"
    }
}
