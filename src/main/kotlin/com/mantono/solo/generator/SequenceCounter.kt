package com.mantono.solo.generator

import java.time.Instant

interface SequenceCounter
{
	/**
	 * @param max the maximum value this SequenceCounter
	 * can reach
	 */
	val max: Long

	/**
	 * Returns the next value in the sequence, or 0 if the sequence
	 * has started over. If this counter has reached the maximum value and
	 * the timestamp remains the same, null will be returned so no new [Id]
	 * can be generated until a new value has been reached for the timestamp
	 * and the sequence counter can reset to zero.
	 */
	fun next(timestamp: TimestampProvider): Pair<Long, Long>?
}

interface TimestampProvider
{
	fun timestamp(): Long
}

object InstantEpochMs: TimestampProvider
{
	override fun timestamp(): Long = Instant.now().epochSecond
}

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