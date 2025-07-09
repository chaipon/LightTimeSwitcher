package jp.superwooo.chaipon.lighttimeswitcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.net.Inet4Address;

public class BOOTReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d("LS", "BOOTReceiver triggered with action: " + action);
        if(intent.ACTION_BOOT_COMPLETED.equals(action)) {
            try {
                if(SchedulePreference.create(context).isEnabled())
                    AlarmScheduler.scheduleAll(context);
            } catch (SecurityException e) {
                Log.e("LS", "scheduling error because of security error");
            }
        }else{
            Log.d("LS", "BOOTReceiver ignored non-boot action: " + action);
        }
    }
}
