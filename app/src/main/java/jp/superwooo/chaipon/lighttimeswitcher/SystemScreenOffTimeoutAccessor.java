package jp.superwooo.chaipon.lighttimeswitcher;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;

public class SystemScreenOffTimeoutAccessor {
    private Context mContext;
    public static SystemScreenOffTimeoutAccessor create(Context context){
        return new SystemScreenOffTimeoutAccessor(context);
    }
    private SystemScreenOffTimeoutAccessor(Context context){
        mContext = context;
    }
    public TimeDurationValue read(){
        ContentResolver cr = mContext.getContentResolver();
        try {
            int timeOut = Settings.System.getInt(cr, Settings.System.SCREEN_OFF_TIMEOUT) / 1000;
            return new TimeDurationValue(timeOut, SettingsActivity.LimitTime);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return new TimeDurationValue(15, SettingsActivity.LimitTime);
        }
    }
    public void write(TimeDurationValue timeout){
        Settings.System.putInt(mContext.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, timeout.milliSecond());
    }
}
