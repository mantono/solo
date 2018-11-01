package com.mantono.solo

import com.mantono.solo.api.SequenceCounter
import com.mantono.solo.api.TimestampProvider

@ExperimentalUnsignedTypes
class Counter(sequenceBits: UInt): SequenceCounter
{
	override val max: ULong = (0L until sequenceBits.toULong()).asSequence()
		.map { 1L shl it.toInt() }
		.sum()
		.toULong()

	private var lastTimestamp: ULong = Long.MIN_VALUE
		set(value)
		{
			if(value > lastTimestamp)
				field = value
		}

	private var counter: ULong = 0
		set(value)
		{
			field = value.coerceAtMost(max + 1)
		}

	override fun next(timestamp: TimestampProvider): Pair<ULong, ULong>?
	{
		if(resetTime(timestamp.timestamp()))
			counter = 0
		else
			++counter

		return if(counter <= max)
			lastTimestamp to counter
		else
			null
	}

	private fun resetTime(timestamp: ULong): Boolean
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