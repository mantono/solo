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

		println(timestampShifted.bitCount())
		timestampShifted.toByteArray().joinToString(separator = "-") { it.toString() }.let { System.out.println(it) }

		val nodeIdShifted: BigInteger = BigInteger(nodeId).abs().let {
			val leftShifts: Int = totalBits - nodeIdBits
			val rightShifts: Int = timestampBits
			(it shl leftShifts) shr rightShifts
		}

		println(nodeIdShifted.bitCount())
		nodeIdShifted.toByteArray().joinToString(separator = "-") { it.toString() }.let { System.out.println(it) }

		val sequenceShifted: BigInteger = sequence.toBigInteger() shl sequenceBits shr sequenceBits

		println(sequenceShifted.bitCount())
		sequenceShifted.toByteArray().joinToString(separator = "-") { it.toString() }.let { System.out.println(it) }

		val outcome: BigInteger = (timestampShifted xor nodeIdShifted xor sequenceShifted)

		println(outcome.bitCount())
		outcome.toByteArray().joinToString(separator = "-") { it.toString() }.let { System.out.println(it) }

		val finalBytes: ByteArray = outcome.toByteArray()
		val bytesDiff: Int = totalBytes - finalBytes.size
		return when
		{
			bytesDiff > 0 -> ByteArray(bytesDiff) { 0 } + finalBytes
			bytesDiff == 0 -> finalBytes
			else -> throw IllegalStateException("Negative diff: $bytesDiff")
		}.also{
			require(it.size == totalBytes) { "Expected $totalBytes bytes, got ${it.size}" }
		}
	}
}

fun Long.toBigInteger(): BigInteger = BigInteger.valueOf(this)