package jp.superwooo.chaipon.lighttimeswitcher;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;

import androidx.preference.PreferenceManager;

import java.util.concurrent.TimeUnit;

public class ShortDurationService extends DurationService {
    @Override
    protected DurationType getTimeDuration() {
        return DurationType.Short;
    }

    @Override
    protected Class getDurationServiceClass() {
        return ShortDurationService.class;
    }
}
