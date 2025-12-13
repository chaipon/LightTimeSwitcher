package jp.superwooo.chaipon.lighttimeswitcher

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import java.time.LocalTime

class EnableTimePreference private constructor(
    private val context: Context,
    private val keyPrefix: String
) {
    private val hourKey = keyPrefix + "hour"
    private val minuteKey = keyPrefix + "minute"
    private val enabledKey = keyPrefix + "enabled"
    private val preference: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context.applicationContext)

    fun save(time: LocalTime, enabled: Boolean) {
        val e = preference.edit()
        e.putBoolean(enabledKey, enabled)
        e.putInt(hourKey, time.hour)
        e.putInt(minuteKey, time.minute)
        e.apply()
    }

    fun save(enabled: Boolean) {
        val e = preference.edit()
        e.putBoolean(enabledKey, enabled)
        e.apply()
    }

    fun loadTime(): LocalTime {
        val hour = preference.getInt(hourKey, 0)
        val minute = preference.getInt(minuteKey, 0)
        return LocalTime.of(hour, minute)
    }

    val isEnabled: Boolean
        get() = preference.getBoolean(enabledKey, false)

    companion object {
        fun create(context: Context, keyPrefix: String): EnableTimePreference {
            return EnableTimePreference(context, keyPrefix)
        }
    }
}
