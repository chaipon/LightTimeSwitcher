package jp.superwooo.chaipon.lighttimeswitcher

class ShortLongTimes(short_duration: Int, long_duration: Int, limit: LimitTime) {
    val shortDuration: TimeDurationValue
    val longDuration: TimeDurationValue

    init {
        var short_duration = short_duration
        if (short_duration > long_duration) short_duration = long_duration
        shortDuration = TimeDurationValue(short_duration, limit)
        longDuration = TimeDurationValue(long_duration, limit)
    }
}
