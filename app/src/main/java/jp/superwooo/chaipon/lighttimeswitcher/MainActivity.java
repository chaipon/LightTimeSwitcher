package jp.superwooo.chaipon.lighttimeswitcher;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
    Integer _minTime = MinTime;
    Integer _maxTime = MaxTime;
    private Integer _timeOut = _minTime;
    private StringBuilder _timeOutMessage = new StringBuilder();
    public boolean isMinimumTimeOut(){
        return _timeOut.equals(_minTime);
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
        _minTime = preferences.getInt(SettingsActivity.MinimumKey, MinTime / 1000) * 1000;
        _maxTime = preferences.getInt(SettingsActivity.MaximumKey, MaxTime / 1000) * 1000;

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
        _timeOutMessage.append(getString(R.string.setting_message, _timeOut /1000));
    }

    private void showTimeOutMessageToToast() {
        makeText(getApplicationContext(), _timeOutMessage.toString(), LENGTH_SHORT).show();
    }

    private void switchTimeOut() {
        ContentResolver cr = getContentResolver();
        try {
            _timeOut = Settings.System.getInt(cr, Settings.System.SCREEN_OFF_TIMEOUT);
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
        }
        if(_timeOut.equals(_minTime)){
            Log.d("timeOut", "set to max");
            _timeOut = _maxTime;
        }else{
            Log.d("timeOut", "set to min ###################### ");
            _timeOut = _minTime;
        }
        Settings.System.putInt(cr, Settings.System.SCREEN_OFF_TIMEOUT, _timeOut);
    }

    public void goToSystemSettings(View view) {
        Intent permissionIntent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        permissionIntent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
        _startLauncher.launch(permissionIntent);
    }
}
