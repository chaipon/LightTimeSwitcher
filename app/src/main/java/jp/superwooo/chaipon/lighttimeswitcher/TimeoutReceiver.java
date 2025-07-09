package jp.superwooo.chaipon.lighttimeswitcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class TimeoutReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String durationTypeName = intent.getStringExtra(AlarmScheduler.DurationTypeKey);
        Log.d("LS", "Time out receive: " + durationTypeName);
        DurationService durationService;
        try {
            durationService =
                    DurationType.valueOf(durationTypeName).create(context.getApplicationContext());
        }catch (Exception e){
            Log.e("LS", "duration type get error" + e.toString());
            durationService =
                    DurationType.Short.create(context.getApplicationContext());
        }
        durationService.setTimeOut();
        AlarmScheduler.scheduleAll(context.getApplicationContext());
    }
}
