package com.mantono.solo.api

interface IdGenerator<out T: Identifier>
{

	/**
	 * Generate an [Identifier] object, based on [nodeId], a timestamp
	 * and the amount of calls for this node within a given time span.
	 *
	 * @param maxWaitTime the maximum amount of milliseconds to wait before throwing
	 * an exception
	 *
	 * @return an [Identifier] of type [T]
	 */
	suspend fun generate(maxWaitTime: Long = 1_000): T
}