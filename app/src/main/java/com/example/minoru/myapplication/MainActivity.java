package com.example.minoru.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.util.Function;
import androidx.core.app.ActivityCompat;

import kotlin.Unit;

import static android.provider.Settings.*;
import static android.widget.Toast.*;
import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_WRITE_SETTINGS = 1;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    //private GoogleApiClient client;
    final Integer MinTime = 15 * 1000;
    final Integer MaxTime = 30 * 60 * 1000;
    private Integer mTimeOut = MinTime;
    private StringBuilder mTimeOutMessage = new StringBuilder();
    // Register the permissions callback, which handles the user's response to the
    // system permissions dialog. Save the return value, an instance of
    // ActivityResultLauncher, as an instance variable.
    private ActivityResultLauncher<String> _requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    execBody();
                } else {
                    showExplainToSetSystemSettings();
                }
            });



    public boolean isMinimumTimeOut(){
        return mTimeOut.equals(MinTime);
    }
    public StringBuilder getTimeoutMessage(){

        return mTimeOutMessage;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Settings.System.canWrite(getApplicationContext()))
            execBody();
        else {
            Intent permissionIntent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            startActivity(permissionIntent);
            this.finish();
        }

    }

    private void showExplainToSetSystemSettings() {
        makeText(getApplicationContext(), "Please set system permission.", LENGTH_SHORT).show();
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
}
