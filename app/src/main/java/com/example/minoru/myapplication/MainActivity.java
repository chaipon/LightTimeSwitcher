package com.example.minoru.myapplication;

import android.app.Activity;
import android.content.ContentResolver;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import static android.provider.Settings.*;
import static android.widget.Toast.*;

public class MainActivity extends Activity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    //private GoogleApiClient client;
    final Integer MinTime = 15 * 1000;
    final Integer MaxTime = 30 * 60 * 1000;
    private Integer mTimeOut = MinTime;
    private StringBuilder mTimeOutMessage = new StringBuilder();

    public boolean isMinimumTimeOut(){
        return mTimeOut.equals(MinTime);
    }
    public StringBuilder getTimeoutMessage(){
        return mTimeOutMessage;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        switchTimeOut();

        makeTimeOutMessage();

        showTimeOutMessageToToast();

        notifyTimeOut();

        this.finish();
    }

    private void notifyTimeOut() {
        NotificationController notification = new NotificationController(this);
        notification.notifyTimeOut();
    }

    private void makeTimeOutMessage() {
        mTimeOutMessage.append(mTimeOut/1000);
        mTimeOutMessage.append(getString(R.string.setting_message));
    }

    private void showTimeOutMessageToToast() {
        makeText(getApplicationContext(), mTimeOutMessage.toString(), LENGTH_SHORT).show();
    }

    private void switchTimeOut() {
        ContentResolver cr = getContentResolver();
        try {
            mTimeOut = Settings.System.getInt(cr, Settings.System.SCREEN_OFF_TIMEOUT);
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
        }
        if(mTimeOut.equals(MinTime)){
            Log.w("timeOut", "set to max");
            mTimeOut = MaxTime;
        }else{
            Log.w("timeOut", "set to min ###################### ");
            mTimeOut = MinTime;
        }
        Settings.System.putInt(cr, Settings.System.SCREEN_OFF_TIMEOUT, mTimeOut);
    }
}
