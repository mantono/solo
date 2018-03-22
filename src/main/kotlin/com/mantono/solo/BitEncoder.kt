package com.mantono.solo

import com.mantono.solo.api.Encoder
import com.mantono.solo.bits.bitsOf
import com.mantono.solo.bits.toByteArray
import com.mantono.solo.id.Id128Bits
import com.mantono.solo.id.Id64Bits
import java.math.BigInteger
import java.nio.ByteBuffer
import java.util.*

data class BitEncoder(val timestampLength: Int, val nodeIdLength: Int, val sequenceLength: Int)
{
	val totalBits: Int = nodeIdLength + timestampLength + sequenceLength
	val totalBytes: Int = totalBits / 8

	val nodeRange: IntRange = 0 until nodeIdLength
	val timestampRange: IntRange = 0 until timestampLength
	val sequenceRange: IntRange = 0 until  sequenceLength

	val maxSequenceValue: Int = run {
		val b = BitSet(sequenceLength)
		b.set(1, sequenceLength - 1)
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
		// Maybe we can do something like
		// timestamp.takeBits(22..63)



		// 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000 // 64


		// 00000000 00000000 01100100 01000010 00011100 00100010 01000100 11101101 // 64
		// 00 01000010 00011100 00100010 01000100 11101101 // << 22 (42 bits)
		// 00010000 10000111 00001000 10010001 00111011 01000000 00000000 00000000 // << 22 (42 bits)
		// 00010000 10000111 00001000 10010001 00111011 01000000 00000000 00000000 // timestampShifted
		// 64 - 42 = 22
		val timestampShifted = timestamp.toBigInteger() shl 22

		// 00000000 00000010 01000100 01011010 00010100 11010010 01011100 11101100 // 64
		// 011100 11101100 // << 52
		// 01110011 10110000 00000000 00000000 00000000 00000000 00000000 00000000
		// 01110011 10110000 00000000 00000000 00000000 00000000 00000000 00000000 // >> 42
		// 00000000 00000000 00000000 00000000 00000000 00111001 11011000 0000000 // nodeIdShifted
		// 64 - 12 = 52
		val nodeIdShifted: BigInteger = (BigInteger(nodeId).abs() shl 52) shr 42

		// 00000000 00000000 00000000 00000000 00000000 00000000 00000000 11101101 // 64
		// 00001110 11010000 00000000 00000000 00000000 00000000 00000000 00000000 // << 54
		// 00000000 00000000 00000000 00000000 00000000 00000000 00000000 11101101 // >> 54
		// 00000000 00000000 00000000 00000000 00000000 00000000 00000000 11101101 // sequenceShiftedLeft
		// 64 - 10 = 54
		val sequenceShiftedLeft: BigInteger = (sequence.toBigInteger() shl 54) shr 54

		// So we got
		// 00010000 10000111 00001000 10010001 00111011 01000000 00000000 00000000 // timestampShifted
		// 00000000 00000000 00000000 00000000 00000000 00111001 11011000 0000000 // nodeIdShifted
		// 00000000 00000000 00000000 00000000 00000000 00000000 00000000 11101101 // sequenceShiftedLeft

		val a: BigInteger = timestampShifted xor nodeIdShifted
		val b: BigInteger = a xor sequenceShiftedLeft

		return b.toByteArray()

		//return (timestampShifted or nodeIdShifted or sequenceShiftedLeft).toByteArray()



//		val nodeBits = nodeId.toBigInteger()
//		val shiftToTotalSize: Int = totalBits - nodeBits.bitCount()
//		val correctSize = nodeBits shl shiftToTotalSize
//		val shiftToRemoveData: Int = nodeBits.bitCount() - nodeIdLength
//		val finaShifted = correctSize shl shiftToRemoveData
//
//
//		val timestampBits = timestamp.toBitSet()
//		val timestampClear: Int = 64 - timestampLength
//		timestampBits.clear(0, timestampClear - 1)
//
//		val sequenceBits = sequence.toBitSet()
//		val sequenceClear: Int = 64 - sequenceLength
//		sequenceBits.clear(0, sequenceClear - 1)


//		val bigSequence = sequence.toBigInteger()
//		val allBits = BigInteger.ZERO shl (totalBits - 1)
//		require(allBits.bitLength() == totalBits)
//		allBits or (bigTimestamp shl )
		TODO("")
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