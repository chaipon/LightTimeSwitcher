package jp.superwooo.chaipon.lighttimeswitcher;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import androidx.preference.PreferenceManager;
import android.util.Log;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import jp.superwooo.chaipon.lighttimeswitcher.R;

public class SettingsActivity extends AppCompatActivity {
    public static final String MinimumKey = "minimumTime";
    public static final String MaximumKey = "maximumTime";
    static final int SettingEnableMinimumTime = 10;
    static final int SettingEnableMaximumTime = 3600 * 24;
    public static final LimitTime limitTime = new LimitTime(SettingEnableMinimumTime, SettingEnableMaximumTime);

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(androidx.appcompat.R.style.Base_Theme_AppCompat);
        setContentView(R.layout.activity_settings);

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
            Log.d("settings", "save minimum: " + minimum);
            Log.d("settings", "save maximum: " + maximum);
            String message = getString(R.string.set_mini_max, minimum, maximum);
            makeText(getApplicationContext(), message, LENGTH_SHORT).show();
        });
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
    }


}
