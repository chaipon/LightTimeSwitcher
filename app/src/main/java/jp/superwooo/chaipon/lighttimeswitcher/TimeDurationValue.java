package jp.superwooo.chaipon.lighttimeswitcher;

public class TimeDurationValue {
    final private  int _milliSecond;
    final private  int _second;
    public TimeDurationValue(int second, LimitTime limit){
        _second = limit.apply(second);
        _milliSecond = _second * 1000;
    }

    @Override
    public boolean equals(Object value) {
        if(this == value) return true;
        if(value == null || getClass() != value.getClass()) return false;

        TimeDurationValue other = (TimeDurationValue)value;
        return _second == other._second;
    }
    @Override
    public int hashCode(){
        return Integer.hashCode(_second);
    }



    public int sec(){return _second;}
    public int milliSecond(){return _milliSecond;}

}
