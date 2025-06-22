package jp.superwooo.chaipon.lighttimeswitcher;

import static android.widget.Toast.makeText;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.PreferenceManager;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import java.time.Duration;
import java.time.LocalTime;

public class SettingsActivity extends AppCompatActivity {
    static final int SettingEnableMinimumTime = 10;
    static final int SettingEnableMaximumTime = 3600 * 24;
    public static final LimitTime LimitTime = new LimitTime(SettingEnableMinimumTime, SettingEnableMaximumTime);
    private Context mContext;
    private final String EnableTimeShortKeyPref = "enable_time_short_";
    private final String EnableTimeLongKeyPref = "enable_time_long_";
    private TimeDurationPreference mTimeDurationPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(androidx.appcompat.R.style.Base_Theme_AppCompat);
        setContentView(R.layout.activity_settings);
        mContext = getApplicationContext();
        mTimeDurationPreference = new TimeDurationPreference(mContext);

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
        findViewById(R.id.checkbox_enable_time_to_set_short).setOnClickListener(v -> {
            if(((CheckBox)v).isChecked())
                enableTime(ShortDurationService.class, R.id.set_short_at, mShortJobId, EnableTimeShortKeyPref);
           else
                disableTime(R.id.set_short_at, mShortJobId, EnableTimeShortKeyPref);
        });
        findViewById(R.id.checkbox_enable_time_to_set_long).setOnClickListener(v -> {
            if(((CheckBox)v).isChecked())
                enableTime(LongDurationService.class, R.id.set_long_at, mLongJobId, EnableTimeLongKeyPref);
            else
                disableTime(R.id.set_long_at, mLongJobId, EnableTimeLongKeyPref);
        });

    }
    private final int mShortJobId = 1;
    private final int mLongJobId = 2;
    private void disableTime(int viewId, int jobId, String prefKey){
        TimePicker timePicker = findViewById(viewId);
        timePicker.setEnabled(true);
        JobScheduler scheduler = (JobScheduler) mContext.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        scheduler.cancel(jobId);
        EnableTimePreference.Create(mContext, prefKey).save(false);
    }
    private void enableTime(Class cls, int viewId, int jobId, String prefKey){
        TimePicker timePicker = findViewById(viewId);
        timePicker.setEnabled(false);
        LocalTime targetTime = LocalTime.of(timePicker.getHour(), timePicker.getMinute());
        LocalTime now = LocalTime.now();

        ComponentName componentName = new ComponentName(mContext, cls);
        JobScheduler scheduler = (JobScheduler) mContext.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        long delayTime = Duration.between(now, targetTime).toMillis();
        if(delayTime < 0)
            Log.e("LS", "Invalid delay time: " + delayTime + ", jobid: " + jobId);
        JobInfo.Builder builder = new JobInfo.Builder(jobId, componentName)
                .setMinimumLatency(delayTime)
                .setPersisted(true);

        Log.d("LS", "schedule delay time: " + delayTime + ", jobid: " + jobId);
        scheduler.cancel(jobId);
        scheduler.schedule(builder.build());

        EnableTimePreference.Create(mContext, prefKey).save(targetTime, true);
    }
     private int parseInt(String inputText, int defaultTime){
        try{
            return Integer.parseInt(inputText);
        }catch(Exception e){
            return defaultTime;
        }
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
    }

    private void loadEnableTimeSettings(String prefixKey, int checkBoxId, int timePickerId) {
        EnableTimePreference enableTimePreference = EnableTimePreference.Create(mContext, prefixKey);

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
