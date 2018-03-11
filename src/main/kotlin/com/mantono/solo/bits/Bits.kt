package com.mantono.solo.bits

import java.util.*
import kotlin.collections.ArrayList
import kotlin.experimental.and
import kotlin.experimental.or

data class Bits(private val bits: BooleanArray = BooleanArray(0)): Iterable<Boolean> by bits.asIterable()
{
	constructor(bits: Int): this(BooleanArray(bits) { false })

	constructor(bits: List<Number>): this(bits.map { it.toByte() }.map { it > 0 }.toBooleanArray())

	val size: Int = bits.size

	override fun equals(other: Any?): Boolean
	{
		if(other !is Bits) return false
		return this.bits.contentEquals(other.bits)
	}

	override fun hashCode(): Int = Arrays.hashCode(bits)

	operator fun get(i: Int): Boolean = bits[i]

	infix fun shl(n: Int): Bits
	{
		val newSize: Int = this.size + n
		val newArray = BooleanArray(newSize) { i ->
			if(i < bits.lastIndex) this[i] else false
		}
		return Bits(newArray)
	}

	infix fun shr(n: Int): Bits
	{
		val newSize: Int = this.size - n
		val newArray = BooleanArray(newSize) { i -> this[i]	}
		return Bits(newArray)
	}

	fun toByteArray(): ByteArray = bitsToBytes(bits).toByteArray()
}

internal tailrec fun bitsToBytes(bits: BooleanArray, bytes: Deque<Byte> = LinkedList(), byteSequence: Int = 0): Deque<Byte>
{
	val start: Int = byteSequence * 8
	val endInclusive: Int = (((byteSequence + 1) * 8) - 1).coerceAtMost(bits.lastIndex)

	if(start > bits.lastIndex) return bytes

	val range: IntRange = start .. endInclusive
	val rangeSize: Int = range.last - range.first
	val singleByte = bits
			.slice(range)
			.mapIndexed { index: Int, b: Boolean ->
				index to (if(b) 1 else 0)
			}
			.onEach { println(it) }
			.map { (index: Int, value: Int) ->
				(value shl (rangeSize - index)).toByte()
			}
			.onEach { println(it) }
			.reduce { acc, byte -> acc or byte }

	bytes.addLast(singleByte)

	return bitsToBytes(bits, bytes, byteSequence + 1)
}