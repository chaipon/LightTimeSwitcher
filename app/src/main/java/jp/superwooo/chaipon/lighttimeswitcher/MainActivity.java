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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import static android.provider.Settings.*;
import static android.widget.Toast.*;

public class MainActivity extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See <a href="https://g.co/AppIndexing/AndroidStudio">...</a> for more information.
     */
    //private GoogleApiClient client;
    public static final Integer MinTime = 15 * 1000;
    public static final Integer MaxTime = 30 * 60 * 1000;
    ShortLongTimes _shortLongTimes;
    private TimeDurationValue _timeOutDuration;
    private StringBuilder _timeOutMessage = new StringBuilder();
    public boolean isMinimumTimeOut(){
        return _timeOutDuration.equals(_shortLongTimes.getShortDuration());
    }
    public StringBuilder getTimeoutMessage(){

        return _timeOutMessage;
    }
    ActivityResultLauncher _requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted ->{
        execBody();
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int shortSec = preferences.getInt(SettingsActivity.MinimumKey, MinTime / 1000);
        int longSec = preferences.getInt(SettingsActivity.MaximumKey, MaxTime / 1000);
        _shortLongTimes = new ShortLongTimes(shortSec, longSec, SettingsActivity.limitTime);

        if(Settings.System.canWrite(getApplicationContext()))
            execBody();
        else {
            setTheme(androidx.appcompat.R.style.Base_Theme_AppCompat);
            setContentView(R.layout.explain_to_setting_system_permissions);
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
        _timeOutMessage.append(getString(R.string.setting_message, _timeOutDuration.sec()));
    }

    private void showTimeOutMessageToToast() {
        makeText(getApplicationContext(), _timeOutMessage.toString(), LENGTH_SHORT).show();
    }

    private void switchTimeOut() {
        ContentResolver cr = getContentResolver();
        try {
            int timeOut = Settings.System.getInt(cr, Settings.System.SCREEN_OFF_TIMEOUT) / 1000;
            _timeOutDuration = new TimeDurationValue(timeOut, SettingsActivity.limitTime);
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
        }
        if(_timeOutDuration.equals(_shortLongTimes.getShortDuration())){
            Log.d("timeOut", "set to max");
            _timeOutDuration = _shortLongTimes.getLongDuration();
        }else{
            Log.d("timeOut", "set to min ###################### ");
            _timeOutDuration = _shortLongTimes.getShortDuration();
        }
        Settings.System.putInt(cr, Settings.System.SCREEN_OFF_TIMEOUT, _timeOutDuration.milliSecond());
    }

    public void goToSystemSettings(View view) {
        Intent permissionIntent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        permissionIntent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
        _startLauncher.launch(permissionIntent);
    }
}
