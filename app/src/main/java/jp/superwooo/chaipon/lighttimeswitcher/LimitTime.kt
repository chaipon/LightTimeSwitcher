package jp.superwooo.chaipon.lighttimeswitcher

import kotlin.math.max
import kotlin.math.min

class LimitTime(private val _min: Int, private val _max: Int) {
    fun apply(value: Int): Int {
        return max(_min.toDouble(), min(_max.toDouble(), value.toDouble())).toInt()
    }
}
