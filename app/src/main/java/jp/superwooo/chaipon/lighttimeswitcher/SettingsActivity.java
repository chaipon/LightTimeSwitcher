package jp.superwooo.chaipon.lighttimeswitcher;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import androidx.preference.PreferenceManager;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.time.Duration;
import java.time.LocalTime;

public class SettingsActivity extends AppCompatActivity {
    public static final String MinimumKey = "minimumTime";
    public static final String MaximumKey = "maximumTime";
    static final int SettingEnableMinimumTime = 10;
    static final int SettingEnableMaximumTime = 3600 * 24;
    public static final LimitTime limitTime = new LimitTime(SettingEnableMinimumTime, SettingEnableMaximumTime);
    private TimePicker _set_long_at;
    private TimePicker _set_short_at;
    private Context _context;
    private final String EnableTimeShortKeyPref = "enable_time_short_";
    private final String EnableTimeLongKeyPref = "enable_time_long_";
    private final String HourKey = "hour";
    private final String MinuteKey = "minute";
    private final String EnableKey = "enable";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(androidx.appcompat.R.style.Base_Theme_AppCompat);
        setContentView(R.layout.activity_settings);
        _context = getApplicationContext();

        LoadSettings();

        findViewById(R.id.applyButton).setOnClickListener(v -> {
            EditText minimumText = findViewById(R.id.editMinimumTime);
            EditText maximumText = findViewById(R.id.editMaximumTime);
            LimitTime limit = new LimitTime(SettingEnableMinimumTime, SettingEnableMaximumTime);
            int shortDuration = parseInt(minimumText.getText().toString(), loadCurrentMinimum());
            int longDuration = parseInt(maximumText.getText().toString(), loadCurrentMaximum());
            ShortLongTimes shortLongTimes = new ShortLongTimes(shortDuration, longDuration, limit);
            minimumText.setText(String.valueOf(shortLongTimes.getShortDuration().sec()));
            maximumText.setText(String.valueOf(shortLongTimes.getLongDuration().sec()));

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor e = preferences.edit();
            int minimum = shortLongTimes.getShortDuration().sec();
            int maximum = shortLongTimes.getLongDuration().sec();
            e.putInt(MinimumKey, minimum);
            e.putInt(MaximumKey, maximum);
            e.apply();
            Log.d("LS", "save minimum: " + minimum);
            Log.d("LS", "save maximum: " + maximum);
            String message = getString(R.string.set_mini_max, minimum, maximum);
            makeText(getApplicationContext(), message, LENGTH_SHORT).show();
        });
        findViewById(R.id.checkbox_enable_time_to_set_short).setOnClickListener(v -> {
            if(((CheckBox)v).isChecked())
                EnableTime(ShortDurationService.class, R.id.set_short_at, _short_job_id, EnableTimeShortKeyPref);
           else
                DisableTime(R.id.set_short_at, _short_job_id, EnableTimeShortKeyPref);
        });
        findViewById(R.id.checkbox_enable_time_to_set_long).setOnClickListener(v -> {
            if(((CheckBox)v).isChecked())
                EnableTime(LongDurationService.class, R.id.set_long_at, _long_job_id, EnableTimeLongKeyPref);
            else
                DisableTime(R.id.set_long_at, _long_job_id, EnableTimeLongKeyPref);
        });

    }
    private final int _short_job_id = 1;
    private final int _long_job_id = 2;
    private void DisableTime(int viewId, int jobId, String prefKey){
        TimePicker timePicker = findViewById(viewId);
        timePicker.setEnabled(true);
        JobScheduler scheduler = (JobScheduler) _context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        scheduler.cancel(jobId);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor e = preferences.edit();
        String enableKey = prefKey + EnableKey;
        e.putBoolean(enableKey, false);
        e.apply();
    }
    private void EnableTime(Class cls,int viewId, int jobId, String prefKey){
        TimePicker timePicker = findViewById(viewId);
        timePicker.setEnabled(false);
        LocalTime targetTime = LocalTime.of(timePicker.getHour(), timePicker.getMinute());
        LocalTime now = LocalTime.now();

        ComponentName componentName = new ComponentName(_context, cls);
        JobScheduler scheduler = (JobScheduler) _context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        long delayTime = Duration.between(now, targetTime).toMillis();
        if(delayTime < 0)
            Log.e("LS", "Invalid delay time: " + delayTime + ", jobid: " + jobId);
        JobInfo.Builder builder = new JobInfo.Builder(jobId, componentName)
                .setMinimumLatency(delayTime)
                .setPersisted(true);

        Log.d("LS", "schedule delay time: " + delayTime + ", jobid: " + jobId);
        scheduler.schedule(builder.build());


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor e = preferences.edit();
        String hourKey = prefKey + HourKey;
        String minuteKey = prefKey + MinuteKey;
        String enableKey = prefKey + EnableKey;
        e.putBoolean(enableKey, true);
        e.putInt(hourKey, timePicker.getHour());
        e.putInt(minuteKey, timePicker.getMinute());
        e.apply();
    }
     private int parseInt(String inputText, int defaultTime){
        try{
            return Integer.parseInt(inputText);
        }catch(Exception e){
            return defaultTime;
        }
    }
    private int loadCurrentMinimum(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int min = preferences.getInt(MinimumKey, MainActivity.MinTime / 1000);
        return min;
    }
    private int loadCurrentMaximum(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int max = preferences.getInt(MaximumKey, MainActivity.MaxTime / 1000);
        return max;
    }

    private void LoadSettings() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        EditText minimumText = findViewById(R.id.editMinimumTime);
        EditText maximumText = findViewById(R.id.editMaximumTime);
        int minimum = preferences.getInt(MinimumKey, MainActivity.MinTime / 1000);
        int maximum = preferences.getInt(MaximumKey, MainActivity.MaxTime / 1000);
        minimumText.setText(String.valueOf(minimum));
        maximumText.setText(String.valueOf(maximum));

        CheckBox shortCheckBox = findViewById(R.id.checkbox_enable_time_to_set_short);
        CheckBox longCheckBox = findViewById(R.id.checkbox_enable_time_to_set_long);
        shortCheckBox.setChecked(preferences.getBoolean(EnableTimeShortKeyPref + EnableKey, false));
        longCheckBox.setChecked(preferences.getBoolean(EnableTimeLongKeyPref + EnableKey, false));

        TimePicker shortPicker = findViewById(R.id.set_short_at);
        TimePicker longPicker = findViewById(R.id.set_long_at);
        shortPicker.setHour(preferences.getInt(EnableTimeShortKeyPref + HourKey, 0));
        shortPicker.setMinute(preferences.getInt(EnableTimeShortKeyPref + MinuteKey, 0));
        shortPicker.setEnabled(!shortCheckBox.isChecked());
        longPicker.setHour(preferences.getInt(EnableTimeLongKeyPref + HourKey, 0));
        longPicker.setMinute(preferences.getInt(EnableTimeLongKeyPref + MinuteKey, 0));
        longPicker.setEnabled(!longCheckBox.isChecked());
    }


}
