package jp.superwooo.chaipon.lighttimeswitcher;

public class ShortLongTimes {
    private final TimeDurationValue mShort;
    private final TimeDurationValue mLong;
    public ShortLongTimes(int short_duration, int long_duration, LimitTime limit){
        if(short_duration > long_duration) short_duration = long_duration;
        mShort = new TimeDurationValue(short_duration, limit);
        mLong = new TimeDurationValue(long_duration, limit);
    }
    public TimeDurationValue getShortDuration(){return mShort;}
    public TimeDurationValue getLongDuration(){return mLong;}
}
