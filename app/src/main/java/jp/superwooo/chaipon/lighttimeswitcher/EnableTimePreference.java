package jp.superwooo.chaipon.lighttimeswitcher;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import java.time.LocalTime;

public class EnableTimePreference {
    private Context mContext;
    private final String mHourKey;
    private final String mMinuteKey;
    private final String mEnabledKey;
    private SharedPreferences mPreference;
    private String mKeyPrefix;

    public static EnableTimePreference Create(Context context, String keyPrefix){
        return new EnableTimePreference(context, keyPrefix);
    }

    private EnableTimePreference(Context context, String keyPrefix){
        mContext = context;
        mPreference = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
        mKeyPrefix = keyPrefix;
        mHourKey = keyPrefix + "hour";
        mMinuteKey = keyPrefix + "minute";
        mEnabledKey = keyPrefix + "enabled";

    }
    public void save(LocalTime time, boolean enabled){
        SharedPreferences.Editor e = mPreference.edit();
        e.putBoolean(mEnabledKey, enabled);
        e.putInt(mHourKey, time.getHour());
        e.putInt(mMinuteKey, time.getMinute());
        e.apply();
    }
    public void save(boolean enabled) {
        SharedPreferences.Editor e = mPreference.edit();
        e.putBoolean(mEnabledKey, enabled);
        e.apply();
    }

    public LocalTime loadTime(){
        int hour = mPreference.getInt(mHourKey, 0);
        int minute = mPreference.getInt(mMinuteKey, 0);
        return LocalTime.of(hour, minute);
    }

    public boolean isEnabled(){
        return mPreference.getBoolean(mEnabledKey, false);
    }
}
