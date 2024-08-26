package jp.superwooo.chaipon.lighttimeswitcher;

public class TimeValue {
    final private  int _milliSecond;
    final private  int _second;
    public TimeValue(int milliSecond){
        _milliSecond = milliSecond;
        _second = milliSecond / 1000;
    }

    public int sec(){return _second;}
    public int milliSecond(){return _milliSecond;}

}
