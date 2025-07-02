package jp.superwooo.chaipon.lighttimeswitcher;

import static android.widget.Toast.makeText;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
    private final ActivityResultLauncher<Intent> mSchedulePermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),result -> {
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return;

                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                if(alarmManager.canScheduleExactAlarms()){
                    mScheduleSwitch.setChecked(true);
                    enableScheduleUI(true);
                    Toast.makeText(this, R.string.enable_schedule, Toast.LENGTH_SHORT).show();
                }else{
                    mScheduleSwitch.setChecked(false);
                    enableScheduleUI(false);
                    Toast.makeText(this, R.string.disable_schedule, Toast.LENGTH_SHORT).show();
                }
            });

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(androidx.appcompat.R.style.Base_Theme_AppCompat);
        setContentView(R.layout.activity_settings);
        mContext = getApplicationContext();
        mTimeDurationPreference = new TimeDurationPreference(mContext);
        mShortTimeSwitch = findViewById(R.id.checkbox_enable_time_to_set_short);
        mLongTimeSwitch = findViewById(R.id.checkbox_enable_time_to_set_long);
        mShortTimePicker = findViewById(R.id.set_short_at);
        mLongTimePicker = findViewById(R.id.set_long_at);
        mScheduleSwitch = findViewById(R.id.checkbox_enable_schedule_func);

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
            if(mShortTimeSwitch.isChecked())
                enableTime(DurationType.Short, R.id.set_short_at, EnableTimeShortKeyPref);
           else
                disableTime(DurationType.Short, R.id.set_short_at, EnableTimeShortKeyPref);
        });
        mLongTimeSwitch.setOnClickListener(v -> {
            if(mLongTimeSwitch.isChecked())
                enableTime(DurationType.Long, R.id.set_long_at, EnableTimeLongKeyPref);
            else
                disableTime(DurationType.Long, R.id.set_long_at, EnableTimeLongKeyPref);
        });
        mScheduleSwitch.setOnClickListener(v -> {
           if(!mScheduleSwitch.isChecked()) {
               enableScheduleUI(false);
               return;
           }
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.S){
                enableScheduleUI(true);
                return;
            }
            showPermissionDialog();
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
                    mScheduleSwitch.setChecked(false);
                })
                .show();
    }
    private void enableScheduleUI(boolean enabled){
        mShortTimeSwitch.setEnabled(enabled);
        mLongTimeSwitch.setEnabled(enabled);
        mShortTimePicker.setEnabled(enabled);
        mLongTimePicker.setEnabled(enabled);

        if(enabled)
            AlarmScheduler.scheduleAll(mContext.getApplicationContext());
        else
            AlarmScheduler.cancelAll(mContext.getApplicationContext());
    }

    private final int mShortJobId = 1;
    private final int mLongJobId = 2;
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
        }catch(Exception e){
            return defaultTime;
        }
    }
    private boolean canScheduleExactAlarms(){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        return alarmManager.canScheduleExactAlarms();
    }

    private int loadCurrentMinimum(){
        return mTimeDurationPreference.getShort().sec();
    }
    private int loadCurrentMaximum(){
        return mTimeDurationPreference.getLong().sec();
    }

    private void LoadSettings() {
        loadTimeDurationSettings();
        loadEnableShortTimeSettings();
        loadEnableLongTimeSettings();
        if(canScheduleExactAlarms()) {
            mScheduleSwitch.setChecked(true);
            enableScheduleUI(true);
        } else {
            mScheduleSwitch.setChecked(false);
            enableScheduleUI(false);
        }
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
