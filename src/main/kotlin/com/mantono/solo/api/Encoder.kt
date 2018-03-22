package com.mantono.solo.api

interface Encoder<out T: Identifier>
{
	val timestampBits: Int
	val nodeIdBits: Int
	val sequenceBits: Int
	fun encode(timestamp: Long, nodeId: ByteArray, sequence: Long): T
}