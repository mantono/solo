package com.mantono.solo.api

interface IdGenerator<out T: Identifier>
{
	/**
	 * An ID that uniquely identifies this generator. This can but must not
	 * necessarily be unique for a physical node. The important thing is
	 * that each generator has an unique id, regardless of from what it is
	 * derived. This ID should remain constant and not change during runtime.
	 */
	val nodeId: ByteArray

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