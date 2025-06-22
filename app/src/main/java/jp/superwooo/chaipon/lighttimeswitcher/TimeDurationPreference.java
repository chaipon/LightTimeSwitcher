package jp.superwooo.chaipon.lighttimeswitcher;

import android.content.SharedPreferences;
import android.content.Context;

import androidx.preference.PreferenceManager;

public class TimeDurationPreference {
    private Context mContext;
    private ShortLongTimes mShortLongTimes;
    public TimeDurationPreference(Context context){
        mContext = context;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
        int shortSec = preferences.getInt(SettingsActivity.MinimumKey, MainActivity.MinTime / 1000);
        int longSec = preferences.getInt(SettingsActivity.MaximumKey, MainActivity.MaxTime / 1000);
        mShortLongTimes = new ShortLongTimes(shortSec, longSec, SettingsActivity.LimitTime);
    }
    public ShortLongTimes getShortLongTimes() {
        return mShortLongTimes;
    }
    public TimeDurationValue getShort(){ return mShortLongTimes.getShortDuration();}
    public TimeDurationValue getLong(){ return mShortLongTimes.getLongDuration();}

    public TimeDurationValue getDurationValue(DurationType type){
        switch (type){
            case  Long: return mShortLongTimes.getLongDuration();
            case Short: return mShortLongTimes.getShortDuration();
            default: throw  new IllegalArgumentException("Unexpected type:" + type);
        }
    }
    public DurationType getType(TimeDurationValue value) {
        if(value.equals(mShortLongTimes.getLongDuration()))
            return DurationType.Long;
        else
            return DurationType.Short;
    }

}
