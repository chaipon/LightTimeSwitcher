package jp.superwooo.chaipon.lighttimeswitcher;

public class LimitTime {
    private final int _min;
    private final int _max;
    public LimitTime(int min, int max){
        _min = min;
        _max = max;
    }
    public int apply(int value){
        return Math.max(_min, Math.min(_max, value));
    }
}
