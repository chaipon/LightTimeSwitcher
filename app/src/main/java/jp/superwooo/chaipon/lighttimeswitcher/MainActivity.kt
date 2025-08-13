package jp.superwooo.chaipon.lighttimeswitcher

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private val mTimeDurationPreference: TimeDurationPreference by lazy { TimeDurationPreference(applicationContext)}
    private var mCurrentTimeoutDuration: TimeDurationValue? = null
    private val timeoutMessage: StringBuilder = StringBuilder()
    private var mRequestPermissionLauncher: ActivityResultLauncher<String>? = null
    private var mStartLauncher: ActivityResultLauncher<Intent>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("LS", "Main activity start")
        super.onCreate(savedInstanceState)
        mRequestPermissionLauncher =
            registerForActivityResult<String, Boolean>(ActivityResultContracts.RequestPermission()) { _: Boolean? ->
                switchTimeOutByUser()
            }
        mStartLauncher =
            registerForActivityResult<Intent, ActivityResult>(ActivityResultContracts.StartActivityForResult()) { _: ActivityResult? ->
                if (Settings.System.canWrite(
                        applicationContext
                    )
                ) mRequestPermissionLauncher!!.launch("android.permission.POST_NOTIFICATIONS")
                else showExplainToSetSystemSettings()
            }

        setCurrentTimeout()
        if (Settings.System.canWrite(applicationContext)) {
            switchTimeOutByUser()
        } else {
            setTheme(androidx.appcompat.R.style.Base_Theme_AppCompat)
            setContentView(R.layout.explain_to_setting_system_permissions)
        }
    }


    private fun showExplainToSetSystemSettings() {
        Toast.makeText(this, "Please set system permission.", Toast.LENGTH_SHORT).show()
    }

    private fun switchTimeOutByUser() {
        setTimeOut(switchedTimeDurationValue)
        makeTimeOutMessage()
        showTimeOutMessageToToast()
        notifyTimeOut()
        this.finish()
    }

    private fun setCurrentTimeout() {
        mCurrentTimeoutDuration =
            SystemScreenOffTimeoutAccessor.Companion.create(applicationContext).read()
    }

    private fun setTimeOut(settingDuration: TimeDurationValue?) {
        if (mCurrentTimeoutDuration == settingDuration) return
        Log.d("LS", "set time out: " + settingDuration!!.sec())
        SystemScreenOffTimeoutAccessor.Companion.create(applicationContext).write(settingDuration)
        mCurrentTimeoutDuration = settingDuration
    }

    private val switchedTimeDurationValue: TimeDurationValue?
        get() {
            if (mCurrentTimeoutDuration == mTimeDurationPreference?.short) {
                Log.d("LS", "set to max")
                return mTimeDurationPreference?.long
            } else {
                Log.d("LS", "set to min")
                return mTimeDurationPreference?.short
            }
        }

    private fun notifyTimeOut() {
        val notification =
            NotificationController(
                applicationContext,
                mTimeDurationPreference!!.getType(mCurrentTimeoutDuration)
            )
        notification.notifyTimeOut()
    }

    private fun makeTimeOutMessage() {
        timeoutMessage.append(getString(R.string.setting_message, mCurrentTimeoutDuration!!.sec()))
    }

    private fun showTimeOutMessageToToast() {
        Toast.makeText(this, timeoutMessage.toString(), Toast.LENGTH_SHORT).show()
    }

    fun goToSystemSettings(view: View?) {
        val permissionIntent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
        permissionIntent.setData(Uri.parse("package:" + applicationContext.packageName))
        mStartLauncher!!.launch(permissionIntent)
    }

    companion object {
        /**
         * ATTENTION: This was auto-generated to implement the App Indexing API.
         * See [...](https://g.co/AppIndexing/AndroidStudio) for more information.
         */
        //private GoogleApiClient client;
        const val MinTime: Int = 15 * 1000
        const val MaxTime: Int = 30 * 60 * 1000
        var DurationTypeKey: String = "DurationType"
    }
}
