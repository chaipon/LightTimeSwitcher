package jp.superwooo.chaipon.lighttimeswitcher.ui

import android.app.AlarmManager
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import jp.superwooo.chaipon.lighttimeswitcher.R
import jp.superwooo.chaipon.lighttimeswitcher.schedule.AlarmScheduler
import jp.superwooo.chaipon.lighttimeswitcher.schedule.EnableTimePreference
import jp.superwooo.chaipon.lighttimeswitcher.schedule.SchedulePreference
import jp.superwooo.chaipon.lighttimeswitcher.screen_timeout.DurationType
import jp.superwooo.chaipon.lighttimeswitcher.screen_timeout.LimitTime
import jp.superwooo.chaipon.lighttimeswitcher.screen_timeout.ShortLongTimes
import jp.superwooo.chaipon.lighttimeswitcher.screen_timeout.TimeDurationPreference
import java.time.LocalTime

class SettingsActivity : AppCompatActivity() {
    private val timeDurationPreference: TimeDurationPreference by lazy{
        TimeDurationPreference(
            applicationContext
        )
    }
    private val scheduleSwitch: CheckBox by lazy {findViewById(R.id.checkbox_enable_schedule_func)}
    private val shortTimeSwitch: CheckBox by lazy {findViewById(R.id.checkbox_enable_time_to_set_short)}
    private val longTimeSwitch: CheckBox by lazy {findViewById(R.id.checkbox_enable_time_to_set_long)}
    private val shortTimePicker: TimePicker by lazy{findViewById(R.id.set_short_at)}
    private val longTimePicker: TimePicker by lazy{findViewById(R.id.set_long_at)}
    private val alarmManager: AlarmManager by lazy{getSystemService(ALARM_SERVICE) as AlarmManager }
    private val schedulePermissionLauncher =
        registerForActivityResult<Intent, ActivityResult>(ActivityResultContracts.StartActivityForResult()) { _: ActivityResult? ->
            if (canScheduleExactAlarms()) {
                setScheduleSwitch(true)
                Toast.makeText(this, R.string.enable_schedule, Toast.LENGTH_SHORT).show()
            } else {
                setScheduleSwitch(false)
                Toast.makeText(this, R.string.disable_schedule, Toast.LENGTH_SHORT).show()
            }
            updateScheduleUIState()
        }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.settings_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_close_settings) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("LS", "Start setting activities")
        setContentView(R.layout.activity_settings)

        LoadSettings()

        findViewById<View>(R.id.applyButton).setOnClickListener { _: View? ->
            val minimumText = findViewById<EditText>(R.id.editMinimumTime)
            val maximumText = findViewById<EditText>(R.id.editMaximumTime)
            val shortDuration = parseInt(minimumText.text.toString(), loadCurrentMinimum())
            val longDuration = parseInt(maximumText.text.toString(), loadCurrentMaximum())
            val shortLongTimes = ShortLongTimes(shortDuration, longDuration, LimitTime)
            minimumText.setText(shortLongTimes.shortDuration.sec().toString())
            maximumText.setText(shortLongTimes.longDuration.sec().toString())
            val message = timeDurationPreference.save(shortLongTimes)
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
        shortTimeSwitch.setOnClickListener(View.OnClickListener { _: View? ->
            if (shortTimeSwitch.isChecked) {
                enableTime(DurationType.Short, R.id.set_short_at, EnableTimeShortKeyPref)
                Toast.makeText(this, R.string.toast_enable_short, Toast.LENGTH_SHORT).show()
            } else {
                disableTime(DurationType.Short, R.id.set_short_at, EnableTimeShortKeyPref)
                Toast.makeText(this, R.string.toast_disable_short, Toast.LENGTH_SHORT).show()
            }
        })
        longTimeSwitch.setOnClickListener(View.OnClickListener { _: View? ->
            if (longTimeSwitch.isChecked) {
                enableTime(DurationType.Long, R.id.set_long_at, EnableTimeLongKeyPref)
                Toast.makeText(this, R.string.toast_enable_long, Toast.LENGTH_SHORT).show()
            } else {
                disableTime(DurationType.Long, R.id.set_long_at, EnableTimeLongKeyPref)
                Toast.makeText(this, R.string.toast_disable_long, Toast.LENGTH_SHORT).show()
            }
        })
        scheduleSwitch.setOnClickListener(View.OnClickListener setOnClickListener@{ _: View? ->
            Log.d("LS", "click schedule check box")
            setScheduleSwitch(scheduleSwitch.isChecked)
            if (requireAlarmPermission()) {
                Log.d("LS", "show permission dialog by click")
                showPermissionDialog()
                return@setOnClickListener
            }
            updateScheduleUIState()
        })
        val openSourceLicenses = findViewById<TextView>(R.id.text_open_source_licenses)
        openSourceLicenses.setOnClickListener {
            OssLicensesMenuActivity.setActivityTitle("Open Source Licences")
            val intent = Intent(this@SettingsActivity, OssLicensesMenuActivity::class.java)
            intent.putExtra("show_all", true)
            try {
                startActivity(intent)
            } catch (e: Exception) {
                Log.e("LS", e.toString())
            }
        }
        val privacyPolicy = findViewById<TextView>(R.id.text_privacy_policy)
        privacyPolicy.setOnClickListener {
            val url = "https://chaipon.github.io/light-time-switcher-policy/"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
    }

    private fun showPermissionDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.schedule_enable_title)
            .setMessage(R.string.explain_schedule_enable_dialog)
            .setPositiveButton(R.string.move_button) { _: DialogInterface?, _: Int ->
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                schedulePermissionLauncher.launch(intent)
            }
            .setNegativeButton(R.string.cancel_button) { _: DialogInterface?, _: Int ->
                setScheduleSwitch(false)
            }
            .show()
    }

    private fun setScheduleSwitch(enabled: Boolean) {
        scheduleSwitch.isChecked = enabled
        SchedulePreference.create(applicationContext).save(enabled)
    }

    private fun updateScheduleUIState() {
        Log.d("LS", "updateScheduleUIState")
        val scheduleEnabled = scheduleSwitch.isChecked

        shortTimeSwitch.isEnabled = scheduleEnabled
        longTimeSwitch.isEnabled = scheduleEnabled

        shortTimePicker.isEnabled = scheduleEnabled && !shortTimeSwitch.isChecked
        longTimePicker.isEnabled = scheduleEnabled && !longTimeSwitch.isChecked

        try {
            if (scheduleEnabled) AlarmScheduler.scheduleAll(applicationContext)
            else AlarmScheduler.cancelAll(applicationContext)
        } catch (e: SecurityException) {
            if (requireAlarmPermission()) {
                Log.d("LS", "show permission dialog by exception")
                showPermissionDialog()
            }
        }
    }

    private fun disableTime(durationType: DurationType, viewId: Int, prefKey: String) {
        val timePicker = findViewById<TimePicker>(viewId)
        timePicker.isEnabled = true
        AlarmScheduler.cancel(applicationContext, durationType)
        EnableTimePreference.create(applicationContext, prefKey).save(false)
    }

    private fun enableTime(type: DurationType, viewId: Int, prefKey: String) {
        val timePicker = findViewById<TimePicker>(viewId)
        timePicker.isEnabled = false
        val targetTime = LocalTime.of(timePicker.hour, timePicker.minute)
        AlarmScheduler.scheduleTimeout(applicationContext, type, targetTime)
        EnableTimePreference.create(applicationContext, prefKey).save(targetTime, true)
    }

    private fun parseInt(inputText: String, defaultTime: Int): Int {
        return try {
            inputText.toInt()
        } catch (e: NumberFormatException) {
            defaultTime
        }
    }

    private fun requireAlarmPermission(): Boolean {
        if (!scheduleSwitch.isChecked) return false
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return false
        return !alarmManager.canScheduleExactAlarms()
    }

    private fun canScheduleExactAlarms(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true
        return alarmManager.canScheduleExactAlarms()
    }

    private fun loadCurrentMinimum(): Int {
        return timeDurationPreference.short.sec()
    }

    private fun loadCurrentMaximum(): Int {
        return timeDurationPreference.long.sec()
    }

    private fun LoadSettings() {
        Log.d("LS", "start load settings.")
        loadTimeDurationSettings()
        loadEnableShortTimeSettings()
        loadEnableLongTimeSettings()
        loadScheduleSwitch()
        updateScheduleUIState()
        Log.d("LS", "end load settings.")
    }

    private fun loadScheduleSwitch() {
        scheduleSwitch.isChecked =
            SchedulePreference.create(applicationContext).isEnabled
    }

    private fun loadEnableTimeSettings(prefixKey: String, checkBoxId: Int, timePickerId: Int) {
        val enableTimePreference: EnableTimePreference =
            EnableTimePreference.create(applicationContext, prefixKey)

        val checkBox = findViewById<CheckBox>(checkBoxId)
        checkBox.isChecked = enableTimePreference.isEnabled

        val timePicker = findViewById<TimePicker>(timePickerId)
        timePicker.hour = enableTimePreference.loadTime().hour
        timePicker.minute = enableTimePreference.loadTime().minute
        timePicker.isEnabled = !checkBox.isChecked
    }

    private fun loadEnableShortTimeSettings() {
        loadEnableTimeSettings(
            EnableTimeShortKeyPref,
            R.id.checkbox_enable_time_to_set_short,
            R.id.set_short_at
        )
    }

    private fun loadEnableLongTimeSettings() {
        loadEnableTimeSettings(
            EnableTimeLongKeyPref,
            R.id.checkbox_enable_time_to_set_long,
            R.id.set_long_at
        )
    }

    private fun loadTimeDurationSettings() {
        val minimumText = findViewById<EditText>(R.id.editMinimumTime)
        val maximumText = findViewById<EditText>(R.id.editMaximumTime)
        minimumText.setText(timeDurationPreference.short.sec().toString())
        maximumText.setText(timeDurationPreference.long.sec().toString())
    }


    companion object {
        private const val SettingEnableMinimumTime: Int = 10
        private const val SettingEnableMaximumTime: Int = 3600 * 24
        val LimitTime: LimitTime = LimitTime(SettingEnableMinimumTime, SettingEnableMaximumTime)
        const val EnableTimeShortKeyPref: String = "enable_time_short_"
        const val EnableTimeLongKeyPref: String = "enable_time_long_"
    }
}