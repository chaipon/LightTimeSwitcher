package jp.superwooo.chaipon.lighttimeswitcher;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import java.util.concurrent.TimeUnit;

public abstract class DurationService extends JobService {
    private TimeDurationValue mCurrentTimeOUtDuration;
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d("LS", "job started: " + params.getJobId());
        new Thread(() -> {
            setCurrentTimeout();
            TimeDurationPreference timeDurationPreference = new TimeDurationPreference(getApplicationContext());
            TimeDurationValue settingDuration = timeDurationPreference.getDurationValue(getTimeDuration());
            setTimeOut(settingDuration);
            NotificationController notificationController =
                    new NotificationController(getApplicationContext(), getTimeDuration());
            notificationController.notifyTimeOut();

            jobFinished(params, false);
            scheduleNextJob(params);
        }).start();
        return true;
    }

    protected abstract DurationType getTimeDuration();
    protected abstract Class getDurationServiceClass();

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
    private void scheduleNextJob(JobParameters params){
        Context context = getApplicationContext();
        ComponentName componentName = new ComponentName(context, getDurationServiceClass());
        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo.Builder builder = new JobInfo.Builder(params.getJobId(), componentName)
                .setMinimumLatency(TimeUnit.HOURS.toMillis(24))
                .setOverrideDeadline(TimeUnit.HOURS.toMillis(24) + 60_000)
                .setPersisted(true);
        scheduler.cancel(params.getJobId());
        scheduler.schedule(builder.build());
    }
    private void setTimeOut(TimeDurationValue settingDuration){
        if(mCurrentTimeOUtDuration.equals(settingDuration)) return;
        Log.d("LS", "set time out: " + settingDuration.sec());
        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, settingDuration.milliSecond());
        mCurrentTimeOUtDuration = settingDuration;
    }
    private void setCurrentTimeout(){
        ContentResolver cr = getContentResolver();
        try {
            int timeOut = Settings.System.getInt(cr, Settings.System.SCREEN_OFF_TIMEOUT) / 1000;
            mCurrentTimeOUtDuration = new TimeDurationValue(timeOut, SettingsActivity.LimitTime);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }
}
