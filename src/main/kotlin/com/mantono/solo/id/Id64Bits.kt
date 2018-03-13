package com.mantono.solo.id

import com.mantono.solo.api.Id64
import com.mantono.solo.bits.asNumber
import toBase64
import java.util.*

class Id64Bits(private val bytes: ByteArray): Id64
{
	override fun asBytes(): ByteArray = bytes.copyOf()
	override fun asString(): String = bytes.toBase64()
	override fun asLong(): Long = bytes.asNumber()

	override fun equals(other: Any?): Boolean
	{
		if(this === other) return true
		if(other !is Id64) return false

		return this.bytes.contentEquals(other.asBytes())
	}

	override fun hashCode(): Int = Arrays.hashCode(bytes)
}