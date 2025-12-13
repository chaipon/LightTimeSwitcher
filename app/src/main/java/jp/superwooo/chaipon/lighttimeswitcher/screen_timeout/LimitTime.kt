package jp.superwooo.chaipon.lighttimeswitcher.screen_timeout

import kotlin.math.max
import kotlin.math.min

class LimitTime(private val min: Int, private val max: Int) {
    fun apply(value: Int): Int {
        return max(min.toDouble(), min(max.toDouble(), value.toDouble())).toInt()
    }
}