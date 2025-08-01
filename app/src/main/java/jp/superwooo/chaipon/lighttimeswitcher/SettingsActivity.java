package jp.superwooo.chaipon.lighttimeswitcher;

import static android.widget.Toast.makeText;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;

import java.time.LocalTime;

public class SettingsActivity extends AppCompatActivity {
    static final int SettingEnableMinimumTime = 10;
    static final int SettingEnableMaximumTime = 3600 * 24;
    public static final LimitTime LimitTime = new LimitTime(SettingEnableMinimumTime, SettingEnableMaximumTime);
    private Context mContext;
    public static  final String EnableTimeShortKeyPref = "enable_time_short_";
    public static final String EnableTimeLongKeyPref = "enable_time_long_";
    private TimeDurationPreference mTimeDurationPreference;
    private CheckBox mScheduleSwitch;
    private CheckBox mShortTimeSwitch;
    private CheckBox mLongTimeSwitch;
    private TimePicker mShortTimePicker;
    private TimePicker mLongTimePicker;
    private AlarmManager mAlarmManager;
    private final ActivityResultLauncher<Intent> mSchedulePermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),result -> {
                if(canScheduleExactAlarms()){
                    setScheduleSwitch(true);
                    Toast.makeText(this, R.string.enable_schedule, Toast.LENGTH_SHORT).show();
                }else{
                    setScheduleSwitch(false);
                    Toast.makeText(this, R.string.disable_schedule, Toast.LENGTH_SHORT).show();
                }
                updateScheduleUIState();
            });

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_close_settings) {
            finish(); // 設定画面を閉じる
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("LS", "Start setting activities");
        setContentView(R.layout.activity_settings);
        mContext = getApplicationContext();
        mTimeDurationPreference = new TimeDurationPreference(mContext);
        mShortTimeSwitch = findViewById(R.id.checkbox_enable_time_to_set_short);
        mLongTimeSwitch = findViewById(R.id.checkbox_enable_time_to_set_long);
        mShortTimePicker = findViewById(R.id.set_short_at);
        mLongTimePicker = findViewById(R.id.set_long_at);
        mScheduleSwitch = findViewById(R.id.checkbox_enable_schedule_func);
        mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        LoadSettings();

        findViewById(R.id.applyButton).setOnClickListener(v -> {
            EditText minimumText = findViewById(R.id.editMinimumTime);
            EditText maximumText = findViewById(R.id.editMaximumTime);
            int shortDuration = parseInt(minimumText.getText().toString(), loadCurrentMinimum());
            int longDuration = parseInt(maximumText.getText().toString(), loadCurrentMaximum());
            ShortLongTimes shortLongTimes = new ShortLongTimes(shortDuration, longDuration, LimitTime);
            minimumText.setText(String.valueOf(shortLongTimes.getShortDuration().sec()));
            maximumText.setText(String.valueOf(shortLongTimes.getLongDuration().sec()));

            mTimeDurationPreference.save(shortLongTimes);
        });
        mShortTimeSwitch.setOnClickListener(v -> {
            if(mShortTimeSwitch.isChecked()) {
                enableTime(DurationType.Short, R.id.set_short_at, EnableTimeShortKeyPref);
                Toast.makeText(this, R.string.toast_enable_short, Toast.LENGTH_SHORT).show();
            }else {
                disableTime(DurationType.Short, R.id.set_short_at, EnableTimeShortKeyPref);
                Toast.makeText(this, R.string.toast_disable_short, Toast.LENGTH_SHORT).show();
            }
        });
        mLongTimeSwitch.setOnClickListener(v -> {
            if(mLongTimeSwitch.isChecked()) {
                enableTime(DurationType.Long, R.id.set_long_at, EnableTimeLongKeyPref);
                Toast.makeText(this, R.string.toast_enable_long, Toast.LENGTH_SHORT).show();
            }else {
                disableTime(DurationType.Long, R.id.set_long_at, EnableTimeLongKeyPref);
                Toast.makeText(this, R.string.toast_disable_long, Toast.LENGTH_SHORT).show();
            }
        });
        mScheduleSwitch.setOnClickListener(v -> {
            Log.d("LS", "click schedule check box");
            setScheduleSwitch(mScheduleSwitch.isChecked());
           if(requireAlarmPermission()) {
               Log.d("LS", "show permission dialog by click");
               showPermissionDialog();
               return;
           }
           updateScheduleUIState();
        });
        TextView openSourceLicenses = findViewById(R.id.text_open_source_licenses);
        openSourceLicenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OssLicensesMenuActivity.setActivityTitle("Open Source Licences");
                Intent intent = new Intent(SettingsActivity.this, OssLicensesMenuActivity.class);
                intent.putExtra("show_all", true);
                try {
                    startActivity(intent);
                }catch (Exception e)
                {
                    Log.e("LS", e.toString());
                }
            }
        });
        TextView privacyPolicy = findViewById(R.id.text_privacy_policy);
        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://chaipon.github.io/light-time-switcher-policy/";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });
    }
    private void showPermissionDialog(){
        new AlertDialog.Builder(this)
                .setTitle(R.string.schedule_enable_title)
                .setMessage(R.string.explain_schedule_enable_dialog)
                .setPositiveButton(R.string.move_button, (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                    mSchedulePermissionLauncher.launch(intent);
                })
                .setNegativeButton(R.string.cancel_button, (dialog, which) -> {
                    setScheduleSwitch(false);
                })
                .show();
    }
    private void setScheduleSwitch(boolean enabled){
        mScheduleSwitch.setChecked(enabled);
        SchedulePreference.create(getApplicationContext()).save(enabled);
    }

    private void updateScheduleUIState(){
        Log.d("LS", "updateScheduleUIState");
        boolean scheduleEnabled = mScheduleSwitch.isChecked();

        mShortTimeSwitch.setEnabled(scheduleEnabled);
        mLongTimeSwitch.setEnabled(scheduleEnabled);

        mShortTimePicker.setEnabled(scheduleEnabled && !mShortTimeSwitch.isChecked());
        mLongTimePicker.setEnabled(scheduleEnabled && !mLongTimeSwitch.isChecked());

        try {
            if (scheduleEnabled)
                AlarmScheduler.scheduleAll(mContext.getApplicationContext());
            else
                AlarmScheduler.cancelAll(mContext.getApplicationContext());
        }catch (SecurityException e){
            if(requireAlarmPermission()) {
                Log.d("LS", "show permission dialog by exception");
                showPermissionDialog();
            }
        }
    }

    private void disableTime(DurationType durationType, int viewId, String prefKey){
        TimePicker timePicker = findViewById(viewId);
        timePicker.setEnabled(true);
        AlarmScheduler.cancel(mContext, durationType);
        EnableTimePreference.create(mContext, prefKey).save(false);
    }
    private void enableTime(DurationType type, int viewId, String prefKey){
        TimePicker timePicker = findViewById(viewId);
        timePicker.setEnabled(false);
        LocalTime targetTime = LocalTime.of(timePicker.getHour(), timePicker.getMinute());
        AlarmScheduler.scheduleTimeout(mContext, type, targetTime);
        EnableTimePreference.create(mContext, prefKey).save(targetTime, true);
    }
     private int parseInt(String inputText, int defaultTime){
        try{
            return Integer.parseInt(inputText);
        }catch(NumberFormatException e){
            return defaultTime;
        }
    }
    private boolean requireAlarmPermission(){
        if(!mScheduleSwitch.isChecked()) return false;
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return false;
        return !mAlarmManager.canScheduleExactAlarms();
    }
    private boolean canScheduleExactAlarms(){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true;
        return mAlarmManager.canScheduleExactAlarms();
    }

    private int loadCurrentMinimum(){
        return mTimeDurationPreference.getShort().sec();
    }
    private int loadCurrentMaximum(){
        return mTimeDurationPreference.getLong().sec();
    }

    private void LoadSettings() {
        Log.d("LS", "start load settings.");
        loadTimeDurationSettings();
        loadEnableShortTimeSettings();
        loadEnableLongTimeSettings();
        loadScheduleSwitch();
        updateScheduleUIState();
        Log.d("LS", "end load settings.");
    }

    private void loadScheduleSwitch() {
        mScheduleSwitch.setChecked(SchedulePreference.create(getApplicationContext()).isEnabled());
    }

    private void loadEnableTimeSettings(String prefixKey, int checkBoxId, int timePickerId) {
        EnableTimePreference enableTimePreference = EnableTimePreference.create(mContext, prefixKey);

        CheckBox checkBox = findViewById(checkBoxId);
        checkBox.setChecked(enableTimePreference.isEnabled());

        TimePicker timePicker = findViewById(timePickerId);
        timePicker.setHour(enableTimePreference.loadTime().getHour());
        timePicker.setMinute(enableTimePreference.loadTime().getMinute());
        timePicker.setEnabled(!checkBox.isChecked());

    }
    private void loadEnableShortTimeSettings() {
        loadEnableTimeSettings(EnableTimeShortKeyPref, R.id.checkbox_enable_time_to_set_short, R.id.set_short_at);
    }
    private void loadEnableLongTimeSettings() {
        loadEnableTimeSettings(EnableTimeLongKeyPref, R.id.checkbox_enable_time_to_set_long, R.id.set_long_at);
    }

    private void loadTimeDurationSettings() {
        EditText minimumText = findViewById(R.id.editMinimumTime);
        EditText maximumText = findViewById(R.id.editMaximumTime);
        minimumText.setText(String.valueOf(mTimeDurationPreference.getShort().sec()));
        maximumText.setText(String.valueOf(mTimeDurationPreference.getLong().sec()));
    }


}
