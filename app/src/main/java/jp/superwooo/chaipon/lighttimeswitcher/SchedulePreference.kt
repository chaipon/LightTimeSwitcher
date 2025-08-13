package jp.superwooo.chaipon.lighttimeswitcher

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class SchedulePreference private constructor(private val mContext: Context) {
    private val mPreference: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(mContext.applicationContext)
    private val mKey = "enabledSchedule"
    fun save(enabled: Boolean) {
        val e = mPreference.edit()
        e.putBoolean(mKey, enabled)
        e.apply()
    }

    val isEnabled: Boolean
        get() = mPreference.getBoolean(mKey, false)

    companion object {
        fun create(context: Context): SchedulePreference {
            return SchedulePreference(context)
        }
    }
}
