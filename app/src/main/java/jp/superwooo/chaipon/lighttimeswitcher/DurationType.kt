package jp.superwooo.chaipon.lighttimeswitcher

import android.content.Context

enum class DurationType {
    Short {
        override fun create(context: Context): DurationService {
            return DurationService(context, this)
        }
    },
    Long {
        override fun create(context: Context): DurationService {
            return DurationService(context, this)
        }
    };

    abstract fun create(context: Context): DurationService
}
