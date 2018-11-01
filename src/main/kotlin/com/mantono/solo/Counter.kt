package com.mantono.solo

import com.mantono.solo.api.SequenceCounter
import com.mantono.solo.api.TimestampProvider

class Counter(sequenceBits: Int) : SequenceCounter {

    init {
        if (sequenceBits <= 0)
            throw IllegalArgumentException("Minimum required bits are 1, but got $sequenceBits")
    }

    override val max: Long = (0L until sequenceBits.toLong()).asSequence()
            .map { 1L shl it.toInt() }
            .sum()

    private var lastTimestamp: Long = Long.MIN_VALUE
        set(value) {
            if (value > lastTimestamp)
                field = value
        }

    private var counter: Long = 0
        set(value) {
            field = value.coerceAtMost(max + 1)
        }

    override fun next(timestamp: TimestampProvider): Pair<Long, Long>? {
        if (resetTime(timestamp.timestamp()))
            counter = 0
        else
            ++counter

        return if (counter <= max)
            lastTimestamp to counter
        else
            null
    }

    private fun resetTime(timestamp: Long): Boolean {
        return if (timestamp > lastTimestamp) {
            this.lastTimestamp = timestamp
            true
        } else
            false
    }
}