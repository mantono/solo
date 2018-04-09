package com.mantono.solo.id

import com.mantono.solo.api.Identifier
import toBase64
import java.util.*

class FlakeId(private val bytes: ByteArray): Identifier
{
	init
	{
		require(bytes.size == 16) { "Expected 16 bytes, got: ${bytes.size}" }
	}

	override fun asBytes(): ByteArray = bytes.copyOf()
	override fun asString(): String = bytes.toBase64()

	override fun equals(other: Any?): Boolean
	{
		if(this === other) return true
		if(other !is Identifier) return false

		return this.bytes.contentEquals(other.asBytes())
	}

	override fun hashCode(): Int = Arrays.hashCode(bytes)
}