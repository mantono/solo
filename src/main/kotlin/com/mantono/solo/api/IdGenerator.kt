package com.mantono.solo.api

import kotlinx.coroutines.experimental.TimeoutCancellationException

interface IdGenerator<out T: Identifier>
{

	/**
	 * Generate an [Identifier] object, based on [nodeId], a timestamp
	 * and the amount of calls for this node within a given time span.
	 *
	 * @param maxWaitTime the maximum amount of milliseconds to wait before throwing
	 * a [TimeoutCancellationException]
	 *
	 * @return an [Identifier] of type [T]
	 */
	suspend fun generate(maxWaitTime: Long = 1_000): T
}