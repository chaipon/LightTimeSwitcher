package jp.superwooo.chaipon.lighttimeswitcher;

public class ShortLongTimes {
    private final TimeDurationValue _short;
    private final TimeDurationValue _long;
    public ShortLongTimes(int short_duration, int long_duration, LimitTime limit){
        if(short_duration > long_duration) short_duration = long_duration;
        _short = new TimeDurationValue(short_duration, limit);
        _long = new TimeDurationValue(long_duration, limit);
    }
    public TimeDurationValue getShortDuration(){return _short;}
    public TimeDurationValue getLongDuration(){return _long;}
}
