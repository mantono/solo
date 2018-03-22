package com.mantono.solo

import com.mantono.solo.api.Encoder
import com.mantono.solo.bits.toByteArray
import com.mantono.solo.id.Id128Bits
import com.mantono.solo.id.Id64Bits
import java.math.BigInteger
import java.nio.ByteBuffer
import java.util.*

data class BitEncoder(val timestampBits: Int, val nodeIdBits: Int, val sequenceBits: Int)
{
	val totalBits: Int = nodeIdBits + timestampBits + sequenceBits
	val totalBytes: Int = totalBits / 8

	val nodeRange: IntRange = 0 until nodeIdBits
	val timestampRange: IntRange = 0 until timestampBits
	val sequenceRange: IntRange = 0 until  sequenceBits

	val maxSequenceValue: Int = run {
		val b = BitSet(sequenceBits)
		b.set(1, sequenceBits - 1)
		b.toLongArray()[0].toInt()
	}

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
		// 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000 // 64


		// 00000000 00000000 01100100 01000010 00011100 00100010 01000100 11101101 // 64
		// 00 01000010 00011100 00100010 01000100 11101101 // << 22 (42 bits)
		// 00010000 10000111 00001000 10010001 00111011 01000000 00000000 00000000 // << 22 (42 bits)
		// 00010000 10000111 00001000 10010001 00111011 01000000 00000000 00000000 // timestampShifted
		// 64 - 42 = 22
		val timestampShifted: BigInteger = timestamp.toBigInteger().let {
			val leftShifts: Int = totalBits - timestampBits
			it shl leftShifts
		}

		// 00000000 00000010 01000100 01011010 00010100 11010010 01011100 11101100 // 64
		// 011100 11101100 // << 52
		// 01110011 10110000 00000000 00000000 00000000 00000000 00000000 00000000
		// 01110011 10110000 00000000 00000000 00000000 00000000 00000000 00000000 // >> 42
		// 00000000 00000000 00000000 00000000 00000000 00111001 11011000 0000000 // nodeIdShifted
		// 64 - 12 = 52
		val nodeIdShifted: BigInteger = BigInteger(nodeId).abs().let {
			val leftShifts: Int = totalBits - nodeIdBits
			val rightShifts: Int = timestampBits
			(it shl leftShifts) shr rightShifts
		}
		// shl 52) shr 42

		// 00000000 00000000 00000000 00000000 00000000 00000000 00000000 11101101 // 64
		// 00001110 11010000 00000000 00000000 00000000 00000000 00000000 00000000 // << 54
		// 00000000 00000000 00000000 00000000 00000000 00000000 00000000 11101101 // >> 54
		// 00000000 00000000 00000000 00000000 00000000 00000000 00000000 11101101 // sequenceShiftedLeft
		// 64 - 10 = 54
		val sequenceShifted: BigInteger = sequence.toBigInteger() shl sequenceBits shr sequenceBits

		// So we got
		// 00010000 10000111 00001000 10010001 00111011 01000000 00000000 00000000 // timestampShifted
		// 00000000 00000000 00000000 00000000 00000000 00111001 11011000 0000000 // nodeIdShifted
		// 00000000 00000000 00000000 00000000 00000000 00000000 00000000 11101101 // sequenceShiftedLeft

		val outcome: BigInteger = (timestampShifted xor nodeIdShifted xor sequenceShifted)
		return outcome.toByteArray()
	}
}

fun ByteArray.toBigInteger(): BigInteger = BigInteger(this)
fun ByteArray.toBitSet(): BitSet = BitSet.valueOf(this)
fun ByteArray.toLong(): Long = BigInteger(this).toLong()
fun Long.toBigInteger(): BigInteger = BigInteger.valueOf(this)
fun Long.toBitSet(): BitSet = BitSet.valueOf(this.toByteArray())

object Default64BitEncoder: Encoder<Id64Bits>
{
	private val enc = BitEncoder(42, 12, 10)

	override val timestampBits: Int = 42
	override val nodeIdBits: Int = 12
	override val sequenceBits: Int = 10

	override fun encode(timestamp: Long, nodeId: ByteArray, sequence: Long): Id64Bits
	{
		return Id64Bits(enc.generateByteArray(timestamp, nodeId, sequence))
	}

}

object Default128BitEncoder: Encoder<Id128Bits>
{
	private val enc = BitEncoder(64, 48, 16)

	override val timestampBits: Int = 64
	override val nodeIdBits: Int = 48
	override val sequenceBits: Int = 16

	override fun encode(timestamp: Long, nodeId: ByteArray, sequence: Long): Id128Bits
	{
		return Id128Bits(enc.generateByteArray(timestamp, nodeId, sequence))
	}

}

fun encodeWithByteBuffer64Bits(node: ByteArray, ts: Long, seq: Long): Id64Bits
{
	val bytes = ByteBuffer.allocate(8)
	bytes.put(node.sliceArray(0..41))
	bytes.put(ts.toByteArray().sliceArray(0..11))
	bytes.put(seq.toByteArray().sliceArray(0..9))
	val byteArray = bytes.array()
	return Id64Bits(byteArray)
}