package com.mantono.solo.encoders

import com.mantono.solo.api.Encoder
import com.mantono.solo.api.Identifier
import java.math.BigInteger

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
		val timestampShifted: BigInteger = timestamp.toBigInteger().let {
			val leftShifts: Int = totalBits - timestampBits
			it shl leftShifts
		}

		val nodeIdShifted: BigInteger = BigInteger(nodeId).abs().let {
			val leftShifts: Int = totalBits - nodeIdBits
			val rightShifts: Int = timestampBits
			(it shl leftShifts) shr rightShifts
		}

		val sequenceShifted: BigInteger = sequence.toBigInteger() shl sequenceBits shr sequenceBits

		val outcome: BigInteger = (timestampShifted xor nodeIdShifted xor sequenceShifted)
		return outcome.toByteArray()
	}
}

fun Long.toBigInteger(): BigInteger = BigInteger.valueOf(this)