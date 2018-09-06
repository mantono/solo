package com.mantono.solo.encoders

import com.mantono.solo.api.Encoder
import com.mantono.solo.api.Identifier
import com.mantono.solo.reduceToLeastSignificantBits
import com.mantono.solo.toBigInteger
import com.mantono.solo.shl
import java.math.BigInteger

@ExperimentalUnsignedTypes
abstract class BitEncoder<out T: Identifier>(
		override val timestampBits: UInt,
		override val nodeIdBits: UInt,
		override val sequenceBits: UInt
): Encoder<T>
{
	val totalBits: UInt = nodeIdBits + timestampBits + sequenceBits
	val totalBytes: UInt = totalBits.div(8u)

	init
	{
		if(totalBits % 8u != 0u)
			throw IllegalArgumentException("The total amount of bits is not a multiple of 8")
	}

	fun assertLength(bits: UInt)
	{
		if(bits != totalBits)
			throw IllegalStateException("Bad bit count: $totalBits != $bits")
	}

	fun generateByteArray(timestamp: ULong, nodeId: ByteArray, sequence: ULong): ByteArray
	{
		val ts: BigInteger =  timestamp.toBigInteger().reduceToLeastSignificantBits(timestampBits - 1).shl(nodeIdBits + sequenceBits)
		val node: BigInteger = BigInteger(nodeId).reduceToLeastSignificantBits(nodeIdBits).shl(sequenceBits)
		val seq: BigInteger = sequence.toBigInteger()

		val outcome: BigInteger = (ts xor node xor seq).abs()
		return outcome.toByteArray().expandTo(totalBytes)
	}
}

@ExperimentalUnsignedTypes
private fun ByteArray.expandTo(newSize: UInt): ByteArray
{
	val bytesDiff: Int = newSize.toInt() - size
	return when
	{
		bytesDiff > 0 -> ByteArray(bytesDiff) { 0 } + this
		bytesDiff == 0 -> this
		else -> throw IllegalStateException("Byte delta is negative")
	}.also {
		require(it.size == newSize.toInt()) { "Expected $newSize bytes, got ${it.size}" }
	}
}