package com.mantono.solo.api

interface SequenceCounter {
    /**
	 * The maximum value this SequenceCounter can reach
	 */
    val max: Long

    /**
	 * Returns a timestamp together with the next value in the sequence, or 0 if the sequence
	 * has started over. If this counter has reached the maximum value and
	 * the timestamp remains the same, null will be returned so no new [Identifier]
	 * can be generated until a new value has been reached for the timestamp
	 * and the sequence counter can reset to zero.
	 *
	 * @param timestamp provides the current timestamp in form of a [Long].
	 *
	 * @return a [Pair] containing a timestamp and a sequence number, or null
	 * if no more sequence numbers can be produced for the given timestamp
	 */
    fun next(timestamp: TimestampProvider): Pair<Long, Long>?
}