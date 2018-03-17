package com.mantono.solo

import com.mantono.solo.api.SequenceCounter
import com.mantono.solo.api.TimestampProvider

class Counter(override val max: Long): SequenceCounter
{
	private var lastTimestamp: Long = Long.MIN_VALUE
		set(value)
		{
			if(value > lastTimestamp)
				field = value
		}

	private var counter: Long = 0
		set(value)
		{
			field = value.coerceAtMost(max)
		}

	override fun next(timestamp: TimestampProvider): Pair<Long, Long>?
	{
		if(resetTime(timestamp.timestamp()))
			counter = 0
		else
			++counter

		return if(counter == max)
			null
		else
			lastTimestamp to counter
	}

	private fun resetTime(timestamp: Long): Boolean
	{
		return if(timestamp > lastTimestamp)
		{
			this.lastTimestamp = timestamp
			true
		}
		else
			false
	}
}