package jp.superwooo.chaipon.lighttimeswitcher;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.minoru.myapplication.R;

public class SettingsActivity extends AppCompatActivity {
    public static final String MinimumKey = "minimumTime";
    public static final String MaximumKey = "maximumTime";
    final int SettingEnableMinimumTime = 10;
    final int SettingEnableMaximumTime = 3600 * 24;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Base_Theme_AppCompat);
        setContentView(R.layout.activity_settings);

        LoadSettings();

        findViewById(R.id.applyButton).setOnClickListener(v -> {
            EditText minimumText = findViewById(R.id.editMinimumTime);
            EditText maximumText = findViewById(R.id.editMaximumTime);
            int minimum = parseInt(minimumText.getText().toString());
            int maximum = parseInt(maximumText.getText().toString());
            if(minimum < SettingEnableMinimumTime) {
                minimumText.setText(String.valueOf(SettingEnableMinimumTime));
                minimum = SettingEnableMinimumTime;
            }
            if(minimum >= SettingEnableMaximumTime) {
                minimumText.setText(String.valueOf(SettingEnableMaximumTime));
                minimum = SettingEnableMaximumTime;
            }
            if(maximum < minimum) {
                maximumText.setText(String.valueOf(minimum));
                maximum = minimum;
            }
            if(maximum >= SettingEnableMaximumTime) {
                maximumText.setText(String.valueOf(SettingEnableMaximumTime));
                maximum = SettingEnableMaximumTime;
            }
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor e = preferences.edit();
            e.putInt(MinimumKey, minimum);
            e.putInt(MaximumKey, maximum);
            e.apply();
            Log.d("settings", "save minimum: " + minimum);
            Log.d("settings", "save maximum: " + maximum);
            String message = getString(R.string.set_mini_max, minimum, maximum);
            makeText(getApplicationContext(), message, LENGTH_SHORT).show();
        });
    }
    private int parseInt(String inputText){
        try{
            return Integer.parseInt(inputText);
        }catch(Exception e){
            return SettingEnableMaximumTime;
        }
    }

    private void LoadSettings() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        EditText minimumText = findViewById(R.id.editMinimumTime);
        EditText maximumText = findViewById(R.id.editMaximumTime);
        int minimum = 0;
        int maximum = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            minimum = preferences.getInt(MinimumKey, MainActivity.MinTime / 1000);
            maximum = preferences.getInt(MaximumKey, MainActivity.MaxTime / 1000);
        }
        minimumText.setText(String.valueOf(minimum));
        maximumText.setText(String.valueOf(maximum));
    }


}
