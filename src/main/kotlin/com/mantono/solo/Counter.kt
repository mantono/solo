package com.mantono.solo

import com.mantono.solo.api.SequenceCounter
import com.mantono.solo.api.TimestampProvider

@ExperimentalUnsignedTypes
class Counter(sequenceBits: UInt): SequenceCounter
{
	init
	{
		if(sequenceBits == 0u)
			throw IllegalArgumentException("Minimum required bits are 1, but got $sequenceBits")
	}

	override val max: ULong = (0uL until sequenceBits.toULong()).asSequence()
			.map { 1uL shl it.toInt() }
			.sumBy { it.toInt() }
			.toULong()

	private var lastTimestamp: ULong = ULong.MIN_VALUE
		set(value)
		{
			if(value > lastTimestamp)
				field = value
		}

	private var counter: ULong = 0u
		set(value)
		{
			field = value.coerceAtMost(max + 1)
		}

	override fun next(timestamp: TimestampProvider): Pair<ULong, ULong>?
	{
		if(resetTime(timestamp.timestamp()))
			counter = 0u
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