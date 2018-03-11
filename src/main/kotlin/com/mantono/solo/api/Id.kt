package com.mantono.solo.api

interface Id
{
	fun entropy(): Int = when(asBytes().size)
	{
		8 -> 64
		16 -> 128
		else -> throw IllegalStateException("Bad entropy size: ${asBytes().size * 8} bits")
	}

	fun asBytes(): ByteArray

	fun asString(): String
}

interface Id128: Id

interface Id64: Id
{
	fun asLong(): Long
}