package com.mantono.solo.id

import com.mantono.solo.api.Identifier
import toBase64
import java.util.*

class Id128Bits(private val bytes: ByteArray): Identifier
{
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