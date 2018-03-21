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

	private val nodeMask: BigInteger = createMask(nodeIdLength)
	private val timestampMask: Long = createMask(timestampLength).toLong()
	private val sequenceMask: Long = 1L shr timestampLength

	private fun createMask(length: Int): BigInteger = BigInteger.ZERO
//	{
//		return (1..length).asSequence()
//				.map { "1" }
//				.joinToString(prefix = "0b", separator = "") { it }
//				.let { BigInteger() }
//	}


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
		// 64 - 42 = 22
		val timestampShifted = timestamp shl 22

		// 64 - 12 = 52
		val nodeIdShifted: Long = nodeId.toLong() shl 52 shr 22
//		val nodeIdShiftedLeft: Long = nodeId.toLong() shl 52
//		val nodeIdShiftedRight: Long = nodeIdShiftedLeft shr 22

		// 64 - 10 = 54
		val sequenceShiftedLeft: Long = sequence shl 54 shr 54
		//val sequenceShiftedRight: Long = sequenceShiftedLeft shr 54

		return (timestampShifted or nodeIdShifted or sequenceShiftedLeft).toByteArray()
//		val and1 = timestampShifted or nodeIdShiftedRight
//		val and2 = and1 or sequenceShiftedRight
//
//		return and2.toByteArray()



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

	override fun invoke(timestamp: Long, nodeId: ByteArray, sequence: Long): Id64Bits
	{
		return Id64Bits(enc.generateByteArray(timestamp, nodeId, sequence))
	}

}

object Default128BitEncoder: Encoder<Id128Bits>
{
	private val enc = BitEncoder(48, 64, 16)

	override fun invoke(timestamp: Long, nodeId: ByteArray, sequence: Long): Id128Bits
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