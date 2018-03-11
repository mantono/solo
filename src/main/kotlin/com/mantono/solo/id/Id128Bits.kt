package com.mantono.solo.id

import com.mantono.solo.api.Id128
import toBase64

class Id128Bits(private val bytes: ByteArray): Id128
{
	override fun asBytes(): ByteArray = bytes.copyOf()
	override fun asString(): String = bytes.toBase64()

	override fun equals(other: Any?): Boolean
	{
		if(this === other) return true
		if(other !is Id128) return false

		return this.bytes.contentEquals(other.asBytes())
	}
}