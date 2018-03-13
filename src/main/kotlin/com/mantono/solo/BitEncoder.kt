package com.mantono.solo

import com.mantono.solo.api.Id
import com.mantono.solo.api.Id128
import com.mantono.solo.api.Id64
import com.mantono.solo.bits.bitsOf
import com.mantono.solo.id.Id128Bits
import com.mantono.solo.id.Id64Bits
import java.util.*

abstract class BitEncoder<out T: Id>(val nodeIdLength: Int, val timestampLength: Int, val sequenceLength: Int)
	{
		val totalBits: Int = nodeIdLength + timestampLength + sequenceLength
		val totalBytes: Int = totalBits / 8

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

		fun generateByteArray(nodeId: ByteArray, timestamp: Long, sequence: Long): ByteArray
		{
			val n = bitsOf(nodeId)
			val t = bitsOf(timestamp)
			val s = bitsOf(sequence)
			n.truncate(nodeIdLength)
			t.truncate(timestampLength)
			s.truncate(sequenceLength)
			val ntAppended = n.append(t)
			val allAppended = ntAppended.append(s)
			return allAppended.toByteArray()
		}

		abstract fun generate(nodeId: ByteArray, timestamp: Long, sequence: Long): T
}

class Bit128Encoder(nodeIdLength: Int, timestampLength: Int, sequenceLength: Int):
		BitEncoder<Id128>(nodeIdLength, timestampLength, sequenceLength)
{

	override fun generate(nodeId: ByteArray, timestamp: Long, sequence: Long): Id128
	{
		return Id128Bits(generateByteArray(nodeId, timestamp, sequence))
	}
}

class Bit64Encoder(nodeIdLength: Int, timestampLength: Int, sequenceLength: Int):
		BitEncoder<Id64>(nodeIdLength, timestampLength, sequenceLength)
{

	override fun generate(nodeId: ByteArray, timestamp: Long, sequence: Long): Id64
	{
		return Id64Bits(generateByteArray(nodeId, timestamp, sequence))
	}
}