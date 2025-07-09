package jp.superwooo.chaipon.lighttimeswitcher;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import static android.widget.Toast.*;

public class MainActivity extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See <a href="https://g.co/AppIndexing/AndroidStudio">...</a> for more information.
     */
    //private GoogleApiClient client;
    public static final Integer MinTime = 15 * 1000;
    public static final Integer MaxTime = 30 * 60 * 1000;
    TimeDurationPreference mTimeDurationPreference;
    private TimeDurationValue mCurrentTimeoutDuration;
    private final StringBuilder mTimeOutMessage = new StringBuilder();
    public StringBuilder getTimeoutMessage(){
        return mTimeOutMessage;
    }
    ActivityResultLauncher<String> mRequestPermissionLauncher;
    ActivityResultLauncher<Intent> mStartLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("LS", "Main activity start");
        super.onCreate(savedInstanceState);
        mRequestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted ->{
            switchTimeOutByUser();
        });
        mStartLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->{
            if(Settings.System.canWrite(getApplicationContext()))
                mRequestPermissionLauncher.launch("android.permission.POST_NOTIFICATIONS");
            else
                showExplainToSetSystemSettings();
        });
        mTimeDurationPreference = new TimeDurationPreference(getApplicationContext());

        setCurrentTimeout();
        if(Settings.System.canWrite(getApplicationContext())) {
            switchTimeOutByUser();
        } else {
            setTheme(androidx.appcompat.R.style.Base_Theme_AppCompat);
            setContentView(R.layout.explain_to_setting_system_permissions);
        }
    }


    private void showExplainToSetSystemSettings() {
        makeText(this, "Please set system permission.", LENGTH_SHORT).show();
    }

    private void switchTimeOutByUser(){
        setTimeOut(getSwitchedTimeDurationValue());
        makeTimeOutMessage();
        showTimeOutMessageToToast();
        notifyTimeOut();
        this.finish();
    }
    private void setCurrentTimeout(){
        mCurrentTimeoutDuration = SystemScreenOffTimeoutAccessor.create(getApplicationContext()).read();
    }

    private void setTimeOut(TimeDurationValue settingDuration){
        if(mCurrentTimeoutDuration.equals(settingDuration)) return;
        Log.d("LS", "set time out: " + settingDuration.sec());
        SystemScreenOffTimeoutAccessor.create(getApplicationContext()).write(settingDuration);
        mCurrentTimeoutDuration = settingDuration;
    }
    private TimeDurationValue getSwitchedTimeDurationValue() {
        if(mCurrentTimeoutDuration.equals(mTimeDurationPreference.getShort())){
            Log.d("LS", "set to max");
            return mTimeDurationPreference.getLong();
        }else{
            Log.d("LS", "set to min");
            return mTimeDurationPreference.getShort();
        }
    }

    public static String DurationTypeKey = "DurationType";

    private void notifyTimeOut() {
        NotificationController notification =
                new NotificationController(getApplicationContext(),
                        mTimeDurationPreference.getType(mCurrentTimeoutDuration));
        notification.notifyTimeOut();
    }

    private void makeTimeOutMessage() {
        mTimeOutMessage.append(getString(R.string.setting_message, mCurrentTimeoutDuration.sec()));
    }

    private void showTimeOutMessageToToast() {
        makeText(this, mTimeOutMessage.toString(), LENGTH_SHORT).show();
    }

    public void goToSystemSettings(View view) {
        Intent permissionIntent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        permissionIntent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
        mStartLauncher.launch(permissionIntent);
    }
}
