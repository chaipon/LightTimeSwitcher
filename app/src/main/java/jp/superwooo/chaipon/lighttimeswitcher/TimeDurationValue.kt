package jp.superwooo.chaipon.lighttimeswitcher

class TimeDurationValue(second: Int, limit: LimitTime) {
    private val mMilliSecond: Int
    private val mSecond = limit.apply(second)

    init {
        mMilliSecond = mSecond * 1000
    }

    override fun equals(value: Any?): Boolean {
        if (this === value) return true
        if (value == null || javaClass != value.javaClass) return false

        val other = value as TimeDurationValue
        return mSecond == other.mSecond
    }

    override fun hashCode(): Int {
        return Integer.hashCode(mSecond)
    }


    fun sec(): Int {
        return mSecond
    }

    fun milliSecond(): Int {
        return mMilliSecond
    }
}
