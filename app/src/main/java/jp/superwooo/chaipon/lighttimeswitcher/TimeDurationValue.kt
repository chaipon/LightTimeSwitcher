package jp.superwooo.chaipon.lighttimeswitcher

class TimeDurationValue(second: Int, limit: LimitTime) {
    private val milliSecond: Int
    private val second = limit.apply(second)

    init {
        milliSecond = this.second * 1000
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val otherValue = other as TimeDurationValue
        return second == otherValue.second
    }

    override fun hashCode(): Int {
        return Integer.hashCode(second)
    }


    fun sec(): Int {
        return second
    }

    fun milliSecond(): Int {
        return milliSecond
    }
}
