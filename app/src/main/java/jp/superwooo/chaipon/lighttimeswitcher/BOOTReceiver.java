package jp.superwooo.chaipon.lighttimeswitcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BOOTReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmScheduler.scheduleAll(context);
    }
}
