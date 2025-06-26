package jp.superwooo.chaipon.lighttimeswitcher;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

import android.content.SharedPreferences;
import android.content.Context;
import android.util.Log;

import androidx.preference.PreferenceManager;

public class TimeDurationPreference {
    private Context mContext;
    private ShortLongTimes mShortLongTimes;
    private static final String MinimumKey = "minimumTime";
    private static final String MaximumKey = "maximumTime";
    public TimeDurationPreference(Context context){
        mContext = context;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        int shortSec = preferences.getInt(MinimumKey, MainActivity.MinTime / 1000);
        int longSec = preferences.getInt(MaximumKey, MainActivity.MaxTime / 1000);
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
    public void save(ShortLongTimes shortLongTimes)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor e = preferences.edit();
        int minimum = shortLongTimes.getShortDuration().sec();
        int maximum = shortLongTimes.getLongDuration().sec();
        e.putInt(MinimumKey, minimum);
        e.putInt(MaximumKey, maximum);
        e.apply();
        Log.d("LS", "save minimum: " + minimum);
        Log.d("LS", "save maximum: " + maximum);
        String message = mContext.getString(R.string.set_mini_max, minimum, maximum);
        makeText(mContext, message, LENGTH_SHORT).show();
    }

}
