package com.mantono.solo.encoders

import com.mantono.solo.api.Encoder
import com.mantono.solo.api.Identifier
import java.math.BigInteger
import kotlin.math.absoluteValue

abstract class BitEncoder<out T: Identifier>(override val timestampBits: Int, override val nodeIdBits: Int, override val sequenceBits: Int): Encoder<T>
{
	val totalBits: Int by lazy { nodeIdBits + timestampBits + sequenceBits }
	val totalBytes: Int = totalBits / 8

	init
	{
		if(totalBits % 8 != 0)
			throw IllegalArgumentException("The total amount of bits is not a multiple of 8")
	}

	fun assertLength(bits: Int)
	{
		if(bits != totalBits)
			throw IllegalStateException("Bad bit count: $totalBits != $bits")
	}

	fun generateByteArray(timestamp: Long, nodeId: ByteArray, sequence: Long): ByteArray
	{
		val ts: BigInteger = timestamp.toBigInteger().reduceTo(timestampBits - 1).shl(nodeIdBits + sequenceBits)
		val node: BigInteger = BigInteger(nodeId).reduceTo(nodeIdBits).shl(sequenceBits)
		val seq: BigInteger = sequence.toBigInteger()

		val outcome: BigInteger = (ts xor node xor seq).abs()

		println("$ts|$node|$seq")

		val finalBytes: ByteArray = outcome.toByteArray()
		val bytesDiff: Int = totalBytes - finalBytes.size
		return when
		{
			bytesDiff > 0 -> ByteArray(bytesDiff) { 0 } + finalBytes
			bytesDiff == 0 -> finalBytes
			else -> finalBytes.sliceArray(bytesDiff.absoluteValue .. finalBytes.lastIndex)
		}.also {
			require(it.size == totalBytes) { "Expected $totalBytes bytes, got ${it.size}" }
		}
	}
}

fun BigInteger.reduceTo(bits: Int): BigInteger
{
	val rightShifts: Int = (bitLength() - bits).coerceAtLeast(0)
	return shr(rightShifts)
}

fun BigInteger.increaseTo(bits: Int): BigInteger
{
	val leftShifts: Int = (bits - bitLength()).coerceAtLeast(0)
	return shl(leftShifts)
}

fun Long.toBigInteger(): BigInteger = BigInteger.valueOf(this)