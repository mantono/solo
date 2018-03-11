package com.mantono.solo.api

interface IdGenerator
{
	/**
	 * An ID that uniquely identifies this generator. This can but must not
	 * necessarily be unique for a physical node. The important thing is
	 * that each generator has an unique id, regardless of from what it is
	 * derived.
	 */
	val nodeId: ByteArray
}

interface IdGenerator128: IdGenerator
{
	/**
	 * Generate an [Id] object, based on [nodeId], current timestamp in UNIX
	 * epoch in milliseconds and sequenceLength of calls for this node within the
	 * last second.
	 */
	suspend fun generate(maxWaitTime: Long = 1_000): Id128
}

interface IdGenerator64: IdGenerator
{
	/**
	 * Generate an [Id] object, based on [nodeId], current timestamp in UNIX
	 * epoch in milliseconds and sequenceLength of calls for this node within the
	 * last second.
	 */
	suspend fun generate(maxWaitTime: Long = 1_000): Id64
}