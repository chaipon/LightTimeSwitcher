package jp.superwooo.chaipon.lighttimeswitcher

class ShortLongTimes(requestedShortTime: Int, requestedLongTime: Int, limit: LimitTime) {
    val shortDuration: TimeDurationValue
    val longDuration: TimeDurationValue

    init {
        val adjustedShortTime = minOf(requestedShortTime, requestedLongTime)
        shortDuration = TimeDurationValue(adjustedShortTime, limit)
        longDuration = TimeDurationValue(requestedLongTime, limit)
    }
}
