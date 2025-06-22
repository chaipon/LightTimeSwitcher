package jp.superwooo.chaipon.lighttimeswitcher;

public class TimeDurationValue {
    final private  int mMilliSecond;
    final private  int mSecond;
    public TimeDurationValue(int second, LimitTime limit){
        mSecond = limit.apply(second);
        mMilliSecond = mSecond * 1000;
    }

    @Override
    public boolean equals(Object value) {
        if(this == value) return true;
        if(value == null || getClass() != value.getClass()) return false;

        TimeDurationValue other = (TimeDurationValue)value;
        return mSecond == other.mSecond;
    }
    @Override
    public int hashCode(){
        return Integer.hashCode(mSecond);
    }



    public int sec(){return mSecond;}
    public int milliSecond(){return mMilliSecond;}

}
