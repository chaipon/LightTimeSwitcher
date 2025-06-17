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
    public static enum DurationType{ None, Short, Long};

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See <a href="https://g.co/AppIndexing/AndroidStudio">...</a> for more information.
     */
    //private GoogleApiClient client;
    public static final Integer MinTime = 15 * 1000;
    public static final Integer MaxTime = 30 * 60 * 1000;
    ShortLongTimes _shortLongTimes;
    private TimeDurationValue _currentTimeOUtDuration;
    private StringBuilder _timeOutMessage = new StringBuilder();
    public boolean isShortDurationTime(){
        return _currentTimeOUtDuration.equals(_shortLongTimes.getShortDuration());
    }
    public StringBuilder getTimeoutMessage(){
        return _timeOutMessage;
    }
    ActivityResultLauncher _requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted ->{
        switchTimeoutByUser();
    });
    ActivityResultLauncher _startLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->{
        if(Settings.System.canWrite(getApplicationContext()))
            _requestPermissionLauncher.launch("android.permission.POST_NOTIFICATIONS");
        else
            showExplainToSetSystemSettings();
    });

    private Intent makeNotificationSettingIntent(Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
        intent.setPackage(context.getPackageName());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            intent.setIdentifier(String.valueOf(context.getApplicationInfo().uid));
        }
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("LS", "Main activity start");
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int shortSec = preferences.getInt(SettingsActivity.MinimumKey, MinTime / 1000);
        int longSec = preferences.getInt(SettingsActivity.MaximumKey, MaxTime / 1000);
        _shortLongTimes = new ShortLongTimes(shortSec, longSec, SettingsActivity.limitTime);

        setCurrantTimeOut();
        DurationType requiredDuration = getRequiredDurationType();
        Log.d("LS", "duration type: " + requiredDuration.name());
        if(Settings.System.canWrite(getApplicationContext())) {
            switch (requiredDuration) {
                case None:
                    switchTimeoutByUser();
                    break;
                case Short:
                case Long:
                    setRequestedTimeout(requiredDuration);
                    break;
            }
        } else {
            setTheme(androidx.appcompat.R.style.Base_Theme_AppCompat);
            setContentView(R.layout.explain_to_setting_system_permissions);
        }

    }

    private DurationType getRequiredDurationType() {
        Intent intent = getIntent();
        String intentDuration = intent.getStringExtra(DurationTypeKey);
        return intentDuration == null ? DurationType.None : DurationType.valueOf(intentDuration);
    }

    private void showExplainToSetSystemSettings() {
        makeText(getApplicationContext(), "Please set system permission.", LENGTH_SHORT).show();
    }

    private void setRequestedTimeout(DurationType requiredDuration){
        Log.d("LS", "set requested timeout.");
        setTimeOut(getTimeOut(requiredDuration));
        makeTimeOutMessage();
        notifyTimeOut();
        this.finish();
    }

    private void switchTimeoutByUser(){
        setTimeOut(getTimeOut(DurationType.None));
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
    private TimeDurationValue getTimeOut(DurationType requiredDurationType){
        switch(requiredDurationType) {
            case Long:
                return _shortLongTimes.getLongDuration();
            case Short:
                return _shortLongTimes.getShortDuration();
            case None:
            default:
                return getSwitchedTimeDurationValue();
        }

    }

    private void setTimeOut(TimeDurationValue settingDuration){
        if(_currentTimeOUtDuration.equals(settingDuration)) return;
        Log.d("LS", "set time out: " + settingDuration.sec());
        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, settingDuration.milliSecond());
        _currentTimeOUtDuration = settingDuration;
    }
    private TimeDurationValue getSwitchedTimeDurationValue() {
        if(_currentTimeOUtDuration.equals(_shortLongTimes.getShortDuration())){
            Log.d("LS", "set to max");
            return _shortLongTimes.getLongDuration();
        }else{
            Log.d("LS", "set to min ###################### ");
            return _shortLongTimes.getShortDuration();
        }
    }

    public static String DurationTypeKey = "DurationType";

    private void notifyTimeOut() {
        NotificationController notification = new NotificationController(this);
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
