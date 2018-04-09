package com.mantono.solo.id

import com.mantono.solo.api.Identifier
import toBase64
import java.nio.ByteBuffer
import java.util.*

class SnowFlakeId(private val bytes: ByteArray): Identifier
{
	init
	{
		require(bytes.size == 8) { "Expected 8 bytes, got: ${bytes.size}" }
	}

	override fun asBytes(): ByteArray = bytes.copyOf()
	override fun asString(): String = bytes.toBase64()
	fun asLong(): Long = bytes.toLong()

	override fun equals(other: Any?): Boolean
	{
		if(this === other) return true
		if(other !is Identifier) return false

		return this.bytes.contentEquals(other.asBytes())
	}

	override fun hashCode(): Int = Arrays.hashCode(bytes)
}

fun ByteArray.toLong(): Long
{
	val buffer = ByteBuffer.allocate(8)
	buffer.put(this)
	buffer.flip()
	return buffer.getLong(0)
}