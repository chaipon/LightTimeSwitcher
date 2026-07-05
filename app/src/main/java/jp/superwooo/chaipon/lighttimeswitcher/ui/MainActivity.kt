package jp.superwooo.chaipon.lighttimeswitcher.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import jp.superwooo.chaipon.lighttimeswitcher.R
import jp.superwooo.chaipon.lighttimeswitcher.notification.NotificationController
import jp.superwooo.chaipon.lighttimeswitcher.screen_timeout.SystemScreenOffTimeoutAccessor
import jp.superwooo.chaipon.lighttimeswitcher.screen_timeout.TimeDurationPreference
import jp.superwooo.chaipon.lighttimeswitcher.screen_timeout.TimeDurationValue

class MainActivity : AppCompatActivity() {
    private val timeDurationPreference: TimeDurationPreference by lazy {
        TimeDurationPreference(
            applicationContext
        )
    }
    private lateinit var currentTimeoutDuration: TimeDurationValue
    private val timeoutMessage: StringBuilder = StringBuilder()
    private var requestPermissionLauncher =
        registerForActivityResult<String, Boolean>(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if(isGranted)
                switchTimeOutByUser()
            else
                Toast.makeText(this, R.string.explain_system_permission, Toast.LENGTH_LONG).show()
            finish()
        }
    private var startLauncher =
        registerForActivityResult<Intent, ActivityResult>(ActivityResultContracts.StartActivityForResult()) { _: ActivityResult? ->
            if (Settings.System.canWrite(applicationContext))
                requestPermissionLauncher.launch("android.permission.POST_NOTIFICATIONS")
            else
                showExplainToSetSystemSettings()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("LS", "Main activity start")
        super.onCreate(savedInstanceState)
        setCurrentTimeout()
        if (Settings.System.canWrite(applicationContext)) {
            switchTimeOutByUser()
        } else {
            setTheme(androidx.appcompat.R.style.Base_Theme_AppCompat)
            setContentView(R.layout.explain_to_setting_system_permissions)
        }
    }


    private fun showExplainToSetSystemSettings() {
        Toast.makeText(this, R.string.explain_system_permission, Toast.LENGTH_SHORT).show()
    }

    private fun switchTimeOutByUser() {
        setTimeOut(switchedTimeDurationValue)
        makeTimeOutMessage()
        showTimeOutMessageToToast()
        notifyTimeOut()
        this.finish()
    }

    private fun setCurrentTimeout() {
        currentTimeoutDuration =
            SystemScreenOffTimeoutAccessor.create(applicationContext).read()
    }

    private fun setTimeOut(settingDuration: TimeDurationValue) {
        if (currentTimeoutDuration == settingDuration) return

        Log.d("LS", "set time out: " + settingDuration.sec())
        SystemScreenOffTimeoutAccessor.create(applicationContext).write(settingDuration)
        currentTimeoutDuration = settingDuration
    }

    private val switchedTimeDurationValue: TimeDurationValue
        get() {
            if (currentTimeoutDuration == timeDurationPreference.short) {
                Log.d("LS", "set to max")
                return timeDurationPreference.long
            } else {
                Log.d("LS", "set to min")
                return timeDurationPreference.short
            }
        }

    private fun notifyTimeOut() {
        val notification =
            NotificationController(
                applicationContext,
                timeDurationPreference.getType(currentTimeoutDuration)
            )
        notification.notifyTimeOut()
    }

    private fun makeTimeOutMessage() {
        timeoutMessage.append(getString(R.string.setting_message, currentTimeoutDuration.sec()))
    }

    private fun showTimeOutMessageToToast() {
        Toast.makeText(this, timeoutMessage.toString(), Toast.LENGTH_SHORT).show()
    }

    @Suppress("UNUSED_PARAMETER")
    fun goToSystemSettings(view: View) {
        val permissionIntent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
        permissionIntent.setData(Uri.parse("package:" + applicationContext.packageName))
        startLauncher.launch(permissionIntent)
    }

    companion object {
        const val MIN_TIME: Int = 15 * 1000
        const val MAX_TIME: Int = 30 * 60 * 1000
    }

}