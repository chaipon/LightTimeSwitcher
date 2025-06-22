package jp.superwooo.chaipon.lighttimeswitcher;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import static android.provider.Settings.*;
import static android.widget.Toast.*;

public class MainActivity extends AppCompatActivity {
    public static enum DurationType{ Short, Long};

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See <a href="https://g.co/AppIndexing/AndroidStudio">...</a> for more information.
     */
    //private GoogleApiClient client;
    public static final Integer MinTime = 15 * 1000;
    public static final Integer MaxTime = 30 * 60 * 1000;
    TimeDurationPreference _timeDurationPreference;
    private TimeDurationValue _currentTimeOUtDuration;
    private StringBuilder _timeOutMessage = new StringBuilder();
    public StringBuilder getTimeoutMessage(){
        return _timeOutMessage;
    }
    ActivityResultLauncher _requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted ->{
        switchTimeOutByUser();
    });
    ActivityResultLauncher _startLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->{
        if(Settings.System.canWrite(getApplicationContext()))
            _requestPermissionLauncher.launch("android.permission.POST_NOTIFICATIONS");
        else
            showExplainToSetSystemSettings();
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("LS", "Main activity start");
        super.onCreate(savedInstanceState);
        _timeDurationPreference = new TimeDurationPreference(getApplicationContext());

        setCurrantTimeOut();
        if(Settings.System.canWrite(getApplicationContext())) {
            switchTimeOutByUser();
        } else {
            setTheme(androidx.appcompat.R.style.Base_Theme_AppCompat);
            setContentView(R.layout.explain_to_setting_system_permissions);
        }
    }


    private void showExplainToSetSystemSettings() {
        makeText(getApplicationContext(), "Please set system permission.", LENGTH_SHORT).show();
    }

    private void switchTimeOutByUser(){
        setTimeOut(getSwitchedTimeDurationValue());
        makeTimeOutMessage();
        showTimeOutMessageToToast();
        notifyTimeOut();
        this.finish();
    }
    private void setCurrantTimeOut(){
        ContentResolver cr = getContentResolver();
        try {
            int timeOut = Settings.System.getInt(cr, Settings.System.SCREEN_OFF_TIMEOUT) / 1000;
            _currentTimeOUtDuration = new TimeDurationValue(timeOut, SettingsActivity.limitTime);
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void setTimeOut(TimeDurationValue settingDuration){
        if(_currentTimeOUtDuration.equals(settingDuration)) return;
        Log.d("LS", "set time out: " + settingDuration.sec());
        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, settingDuration.milliSecond());
        _currentTimeOUtDuration = settingDuration;
    }
    private TimeDurationValue getSwitchedTimeDurationValue() {
        if(_currentTimeOUtDuration.equals(_timeDurationPreference.getShort())){
            Log.d("LS", "set to max");
            return _timeDurationPreference.getLong();
        }else{
            Log.d("LS", "set to min");
            return _timeDurationPreference.getShort();
        }
    }

    public static String DurationTypeKey = "DurationType";

    private void notifyTimeOut() {
        NotificationController notification =
                new NotificationController(getApplicationContext(),
                        _timeDurationPreference.getType(_currentTimeOUtDuration));
        notification.notifyTimeOut();
    }

    private void makeTimeOutMessage() {
        _timeOutMessage.append(getString(R.string.setting_message, _currentTimeOUtDuration.sec()));
    }

    private void showTimeOutMessageToToast() {
        makeText(getApplicationContext(), _timeOutMessage.toString(), LENGTH_SHORT).show();
    }

    public void goToSystemSettings(View view) {
        Intent permissionIntent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        permissionIntent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
        _startLauncher.launch(permissionIntent);
    }
}
