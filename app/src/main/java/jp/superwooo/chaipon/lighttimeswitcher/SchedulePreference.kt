package jp.superwooo.chaipon.lighttimeswitcher

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class SchedulePreference private constructor(private val context: Context) {
    private val preference: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
    private val key = "enabledSchedule"
    fun save(enabled: Boolean) {
        val e = preference.edit()
        e.putBoolean(key, enabled)
        e.apply()
    }

    val isEnabled: Boolean
        get() = preference.getBoolean(key, false)

    companion object {
        fun create(context: Context): SchedulePreference {
            return SchedulePreference(context)
        }
    }
}
