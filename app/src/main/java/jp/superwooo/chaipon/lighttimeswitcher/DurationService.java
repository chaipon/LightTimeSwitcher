package jp.superwooo.chaipon.lighttimeswitcher;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Log;

import androidx.preference.PreferenceManager;

import java.util.concurrent.TimeUnit;

public abstract class DurationService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d("LS", "job started: " + params.getJobId());
        new Thread(() -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra(MainActivity.DurationTypeKey, getTimeDuration().name());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.d("LS", "start activity: " + intent.getStringExtra(MainActivity.DurationTypeKey));
            startActivity(intent);

            jobFinished(params, false);
            scheduleNextJob(params);
        }).start();
        return true;
    }

    protected abstract MainActivity.DurationType getTimeDuration();

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
    private void scheduleNextJob(JobParameters params){
        Context context = getApplicationContext();
        ComponentName componentName = new ComponentName(context, LongDurationService.class);
        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo.Builder builder = new JobInfo.Builder(params.getJobId(), componentName)
                .setMinimumLatency(TimeUnit.HOURS.toMillis(24))
                .setPersisted(true);
        scheduler.cancel(params.getJobId());
        scheduler.schedule(builder.build());
    }
}
