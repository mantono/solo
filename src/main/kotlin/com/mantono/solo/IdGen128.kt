package com.mantono.solo

import com.mantono.solo.api.Id128
import com.mantono.solo.api.IdGenerator128
import com.mantono.solo.bits.Bits
import com.mantono.solo.bits.bitsOf
import kotlinx.coroutines.experimental.TimeoutCancellationException
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.channels.sendBlocking
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import toBase64
import java.nio.ByteBuffer
import java.time.Instant
import java.util.*
import java.util.concurrent.TimeUnit


class IdGen128(buffer: Int = 1000, parallelism: Int = 1): IdGenerator128
{
	override val nodeId: ByteArray = getMacAddress()

	private val idChannel: Channel<Id128> = Channel(buffer)

	init
	{
		launch { idGenerator(nodeId, idChannel) }
	}

	override suspend fun generate(maxWaitTime: Long): Id128
	{
		val throwException = async {
			delay(maxWaitTime, TimeUnit.MILLISECONDS)
			throw TimeoutCancellationException("$maxWaitTime milliseconds passed without getting an ID")
		}
		val id: Id128 = idChannel.receive()
		throwException.cancel()
		return id
	}
}

private val encoder = BitEncoder(64, 48, 16)

internal tailrec suspend fun idGenerator(
		nodeId: ByteArray,
		channel: SendChannel<Id128>,
		lastGenerated: Long = Instant.now().toEpochMilli(),
		sequence: Long = 0
)
{
	val now: Long = epochMs()
	if(sequence < encoder.maxSequenceValue)
	{
		val idBytes: ByteArray = encoder.generate(nodeId, now, sequence)
		channel.send(Id128Bits(idBytes))
	}

	when
	{
		lastGenerated == now -> idGenerator(nodeId, channel, now, sequence + 1)
		lastGenerated < now -> idGenerator(nodeId, channel, now, 0)
		else -> throw IllegalStateException("Unhandled time shift occurred")
	}
}

private fun epochMs(): Long = Instant.now().toEpochMilli()

data class BitEncoder(val nodeIdLength: Int, val timestampLength: Int, val sequenceLength: Int)
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

	fun generate(nodeId: ByteArray, timestamp: Long, sequence: Long): ByteArray
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

	private fun trim(nodeBits: BitSet, timestampBits: BitSet, sequenceBits: BitSet): ByteArray
	{
		return merge(
				nodeBits.slice(nodeIdLength),
				timestampBits.slice(timestampLength),
				sequenceBits.slice(sequenceLength)
		)
	}

	private fun merge(nodeBits: BitSet, timestampBits: BitSet, sequenceBits: BitSet): ByteArray
	{
		val bitSize: Int = sequenceOf(nodeBits, timestampBits, sequenceBits).map { it.length() }.onEach { println(it) }.sum()
		if(bitSize != totalBits)
			throw IllegalArgumentException("Size not matching: $bitSize != $totalBits")
		return nodeBits
				.append(timestampBits)
				.append(sequenceBits)
				.toByteArray()
	}
}

private fun BitSet.append(bs: BitSet): BitSet
{
	val totalSize: Int = this.size() + bs.size()
	val newSet = BitSet(totalSize)
	this.stream().forEach { newSet.set(it) }
	newSet.or(bs)
	return newSet
}

private fun BitSet.slice(newSize: Int): BitSet
{
	val newSet = BitSet(newSize)
	(0 until newSize).forEach { i -> if(this[i]) newSet.set(i) }
	return newSet
}

fun bitMask(totalBits: Int, startIndex: Int, endExclusiveIndex: Int): BitSet
{
	val bitMask = BitSet(totalBits)
	bitMask.flip(startIndex, endExclusiveIndex)
	return bitMask
}

class Id128Bits(private val bytes: ByteArray): Id128
{
	override fun asBytes(): ByteArray = bytes.copyOf()
	override fun asString(): String = bytes.toBase64()

	override fun equals(other: Any?): Boolean
	{
		if(this === other) return true
		if(other !is Id128) return false

		return this.bytes.contentEquals(other.asBytes())
	}
}