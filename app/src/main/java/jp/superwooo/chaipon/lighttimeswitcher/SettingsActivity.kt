package jp.superwooo.chaipon.lighttimeswitcher

import android.app.AlarmManager
import android.content.Context
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
import jp.superwooo.chaipon.lighttimeswitcher.AlarmScheduler.cancel
import jp.superwooo.chaipon.lighttimeswitcher.AlarmScheduler.cancelAll
import jp.superwooo.chaipon.lighttimeswitcher.AlarmScheduler.scheduleAll
import jp.superwooo.chaipon.lighttimeswitcher.AlarmScheduler.scheduleTimeout
import java.time.LocalTime

class SettingsActivity : AppCompatActivity() {
    private val mTimeDurationPreference: TimeDurationPreference by lazy{TimeDurationPreference(applicationContext)}
    private val mScheduleSwitch: CheckBox by lazy {findViewById(R.id.checkbox_enable_schedule_func)}
    private val mShortTimeSwitch: CheckBox by lazy {findViewById(R.id.checkbox_enable_time_to_set_short)}
    private val mLongTimeSwitch: CheckBox by lazy {findViewById(R.id.checkbox_enable_time_to_set_long)}
    private val mShortTimePicker: TimePicker by lazy{findViewById(R.id.set_short_at)}
    private val mLongTimePicker: TimePicker by lazy{findViewById(R.id.set_long_at)}
    private val mAlarmManager: AlarmManager by lazy{getSystemService(ALARM_SERVICE) as AlarmManager}
    private val mSchedulePermissionLauncher =
        registerForActivityResult<Intent, ActivityResult>(ActivityResultContracts.StartActivityForResult()) {_: ActivityResult? ->
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
            finish() // 設定画面を閉じる
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("LS", "Start setting activities")
        setContentView(R.layout.activity_settings)

        LoadSettings()

        findViewById<View>(R.id.applyButton).setOnClickListener { v: View? ->
            val minimumText = findViewById<EditText>(R.id.editMinimumTime)
            val maximumText = findViewById<EditText>(R.id.editMaximumTime)
            val shortDuration = parseInt(minimumText.text.toString(), loadCurrentMinimum())
            val longDuration = parseInt(maximumText.text.toString(), loadCurrentMaximum())
            val shortLongTimes = ShortLongTimes(shortDuration, longDuration, LimitTime)
            minimumText.setText(shortLongTimes.shortDuration.sec().toString())
            maximumText.setText(shortLongTimes.longDuration.sec().toString())
            mTimeDurationPreference.save(shortLongTimes)
        }
        mShortTimeSwitch.setOnClickListener(View.OnClickListener { v: View? ->
            if (mShortTimeSwitch.isChecked) {
                enableTime(DurationType.Short, R.id.set_short_at, EnableTimeShortKeyPref)
                Toast.makeText(this, R.string.toast_enable_short, Toast.LENGTH_SHORT).show()
            } else {
                disableTime(DurationType.Short, R.id.set_short_at, EnableTimeShortKeyPref)
                Toast.makeText(this, R.string.toast_disable_short, Toast.LENGTH_SHORT).show()
            }
        })
        mLongTimeSwitch.setOnClickListener(View.OnClickListener { v: View? ->
            if (mLongTimeSwitch.isChecked) {
                enableTime(DurationType.Long, R.id.set_long_at, EnableTimeLongKeyPref)
                Toast.makeText(this, R.string.toast_enable_long, Toast.LENGTH_SHORT).show()
            } else {
                disableTime(DurationType.Long, R.id.set_long_at, EnableTimeLongKeyPref)
                Toast.makeText(this, R.string.toast_disable_long, Toast.LENGTH_SHORT).show()
            }
        })
        mScheduleSwitch.setOnClickListener(View.OnClickListener setOnClickListener@{ _: View? ->
            Log.d("LS", "click schedule check box")
            setScheduleSwitch(mScheduleSwitch.isChecked)
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
            .setPositiveButton(R.string.move_button) { dialog: DialogInterface?, which: Int ->
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                mSchedulePermissionLauncher.launch(intent)
            }
            .setNegativeButton(R.string.cancel_button) { dialog: DialogInterface?, which: Int ->
                setScheduleSwitch(false)
            }
            .show()
    }

    private fun setScheduleSwitch(enabled: Boolean) {
        mScheduleSwitch.isChecked = enabled
        SchedulePreference.Companion.create(applicationContext).save(enabled)
    }

    private fun updateScheduleUIState() {
        Log.d("LS", "updateScheduleUIState")
        val scheduleEnabled = mScheduleSwitch.isChecked

        mShortTimeSwitch.isEnabled = scheduleEnabled
        mLongTimeSwitch.isEnabled = scheduleEnabled

        mShortTimePicker.isEnabled = scheduleEnabled && !mShortTimeSwitch.isChecked
        mLongTimePicker.isEnabled = scheduleEnabled && !mLongTimeSwitch.isChecked

        try {
            if (scheduleEnabled) scheduleAll(applicationContext)
            else cancelAll(applicationContext)
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
        cancel(applicationContext, durationType)
        EnableTimePreference.Companion.create(applicationContext, prefKey).save(false)
    }

    private fun enableTime(type: DurationType, viewId: Int, prefKey: String) {
        val timePicker = findViewById<TimePicker>(viewId)
        timePicker.isEnabled = false
        val targetTime = LocalTime.of(timePicker.hour, timePicker.minute)
        scheduleTimeout(applicationContext, type, targetTime)
        EnableTimePreference.Companion.create(applicationContext, prefKey).save(targetTime, true)
    }

    private fun parseInt(inputText: String, defaultTime: Int): Int {
        return try {
            inputText.toInt()
        } catch (e: NumberFormatException) {
            defaultTime
        }
    }

    private fun requireAlarmPermission(): Boolean {
        if (!mScheduleSwitch.isChecked) return false
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return false
        return !mAlarmManager.canScheduleExactAlarms()
    }

    private fun canScheduleExactAlarms(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true
        return mAlarmManager.canScheduleExactAlarms()
    }

    private fun loadCurrentMinimum(): Int {
        return mTimeDurationPreference.short.sec()
    }

    private fun loadCurrentMaximum(): Int {
        return mTimeDurationPreference.long.sec()
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
        mScheduleSwitch.isChecked =
            SchedulePreference.Companion.create(applicationContext).isEnabled
    }

    private fun loadEnableTimeSettings(prefixKey: String, checkBoxId: Int, timePickerId: Int) {
        val enableTimePreference: EnableTimePreference =
            EnableTimePreference.Companion.create(applicationContext, prefixKey)

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
        minimumText.setText(mTimeDurationPreference.short.sec().toString())
        maximumText.setText(mTimeDurationPreference.long.sec().toString())
    }


    companion object {
        private const val SettingEnableMinimumTime: Int = 10
        private const val SettingEnableMaximumTime: Int = 3600 * 24
        val LimitTime: LimitTime = LimitTime(SettingEnableMinimumTime, SettingEnableMaximumTime)
        const val EnableTimeShortKeyPref: String = "enable_time_short_"
        const val EnableTimeLongKeyPref: String = "enable_time_long_"
    }
}
