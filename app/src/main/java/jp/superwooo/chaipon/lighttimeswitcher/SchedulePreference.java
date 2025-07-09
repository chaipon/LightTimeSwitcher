package jp.superwooo.chaipon.lighttimeswitcher;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import java.time.LocalTime;

public class SchedulePreference {
    private Context mContext;
    private SharedPreferences mPreference;
    private final String mKey = "enabledSchedule";

    public static SchedulePreference
    create(Context context){
        return new SchedulePreference(context);
    }

    private SchedulePreference(Context context){
        mContext = context;
        mPreference = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
    }
    public void save(boolean enabled){
        SharedPreferences.Editor e = mPreference.edit();
        e.putBoolean(mKey, enabled);
        e.apply();
    }

    public boolean isEnabled(){
        return mPreference.getBoolean(mKey, false);
    }
}
