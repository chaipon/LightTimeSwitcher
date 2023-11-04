package com.example.minoru.myapplication;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import static android.provider.Settings.*;
import static android.widget.Toast.*;

@RequiresApi(api = Build.VERSION_CODES.M)
public class MainActivity extends AppCompatActivity {

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
    ActivityResultLauncher mStartLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->{
        if(Settings.System.canWrite(getApplicationContext()))
            execBody();
        else
            showExplainToSetSystemSettings();
    });

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Settings.System.canWrite(getApplicationContext()))
            execBody();
        else {
            setTheme(R.style.Base_Theme_AppCompat);
            setContentView(R.layout.explain_to_setting_system_permissions);
        }

    }

    private void showExplainToSetSystemSettings() {
        makeText(getApplicationContext(), "Please set system permission.", LENGTH_SHORT).show();
        this.finish();
    }

    private void execBody() {
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

    public void goToSystemSettings(View view) {
        Intent permissionIntent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        permissionIntent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
        mStartLauncher.launch(permissionIntent);
    }
}
