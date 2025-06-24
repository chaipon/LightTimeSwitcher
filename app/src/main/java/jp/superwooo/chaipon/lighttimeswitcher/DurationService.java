package jp.superwooo.chaipon.lighttimeswitcher;

import android.content.Context;
import android.util.Log;

public class DurationService {
    private Context mContext;
    private DurationType mDurationType = DurationType.Short;
    public DurationService(Context context, DurationType durationType){
        mContext = context;
        mDurationType = durationType;
    }

    public void setTimeOut(){
        TimeDurationValue currentDuration = SystemScreenOffTimeoutAccessor.create(mContext).read();
        TimeDurationPreference preference = new TimeDurationPreference(mContext);
        TimeDurationValue settingDuration = preference.getDurationValue(mDurationType);
        if(currentDuration.equals(settingDuration)) return;
        Log.d("LS", "set time out: " + settingDuration.sec());
        SystemScreenOffTimeoutAccessor.create(mContext).write(settingDuration);

        NotificationController notificationController =
                new NotificationController(mContext, mDurationType);
        notificationController.notifyTimeOut();
    }
}
