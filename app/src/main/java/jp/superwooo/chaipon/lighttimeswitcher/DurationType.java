package jp.superwooo.chaipon.lighttimeswitcher;

import android.content.Context;

public enum DurationType {
    Short{
        @Override
        public DurationService create(Context context) {
            return new DurationService(context, this);
        }
    },
    Long{
        @Override
        public DurationService create(Context context) {
            return new DurationService(context, this);
        }
    };
    public abstract DurationService create(Context context);
}
