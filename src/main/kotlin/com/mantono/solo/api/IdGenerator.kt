package com.mantono.solo.api

import kotlinx.coroutines.experimental.TimeoutCancellationException
import java.util.concurrent.TimeUnit

interface IdGenerator<out T: Identifier>
{
	/**
	 * Generate an [Identifier] object, based on [nodeId], a timestamp
	 * and the amount of calls for this node within a given time span.
	 *
	 * @param maxWaitTime the maximum amount of time in the given timeunit
	 * to wait before throwing a [TimeoutCancellationException]
	 * @param unit [TimeUnit] for [maxWaitTime]
	 *
	 * @return an [Identifier] of type [T]
	 */
	suspend fun generate(maxWaitTime: Long = 1_000, unit: TimeUnit = TimeUnit.MILLISECONDS): T
}