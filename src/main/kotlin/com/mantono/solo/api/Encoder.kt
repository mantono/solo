package com.mantono.solo.api

@ExperimentalUnsignedTypes
interface Encoder<out T: Identifier>
{
	val timestampBits: UInt
	val nodeIdBits: UInt
	val sequenceBits: UInt
	fun encode(timestamp: ULong, nodeId: ByteArray, sequence: ULong): T
}