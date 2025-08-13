package jp.superwooo.chaipon.lighttimeswitcher

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import java.time.LocalTime

class EnableTimePreference private constructor(
    private val mContext: Context?,
    private val mKeyPrefix: String
) {
    private val mHourKey = mKeyPrefix + "hour"
    private val mMinuteKey = mKeyPrefix + "minute"
    private val mEnabledKey = mKeyPrefix + "enabled"
    private val mPreference: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(mContext!!.applicationContext)

    fun save(time: LocalTime, enabled: Boolean) {
        val e = mPreference.edit()
        e.putBoolean(mEnabledKey, enabled)
        e.putInt(mHourKey, time.hour)
        e.putInt(mMinuteKey, time.minute)
        e.apply()
    }

    fun save(enabled: Boolean) {
        val e = mPreference.edit()
        e.putBoolean(mEnabledKey, enabled)
        e.apply()
    }

    fun loadTime(): LocalTime {
        val hour = mPreference.getInt(mHourKey, 0)
        val minute = mPreference.getInt(mMinuteKey, 0)
        return LocalTime.of(hour, minute)
    }

    val isEnabled: Boolean
        get() = mPreference.getBoolean(mEnabledKey, false)

    companion object {
        fun create(context: Context?, keyPrefix: String): EnableTimePreference {
            return EnableTimePreference(context, keyPrefix)
        }
    }
}
