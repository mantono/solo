package com.mantono.solo.bits

import java.nio.ByteBuffer
import java.util.*
import kotlin.experimental.and
import kotlin.experimental.or

data class Bits(private val bits: BooleanArray = BooleanArray(0)): Iterable<Boolean> by bits.asIterable(), Number()
{
	constructor(bits: Int): this(BooleanArray(bits) { false })

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

	fun append(otherBits: Bits): Bits = sequenceOf(this.bits, otherBits.bits)
			.flatMap { it.asSequence() }
			.toList()
			.toBooleanArray()
			.let { Bits(it) }

	fun reduceToMostSignificantSetBit(): Bits
	{
		val i: Int = bits.indexOf(true)
		if(i < 0)
			return Bits(0)

		return Bits(bits.sliceArray(i .. bits.lastIndex))
	}

	fun padLeftWithZeroes(leftPads: Int): Bits
	{
		if(leftPads < 0)
			throw IllegalArgumentException("Negative padding not allowed")
		if(leftPads == 0)
			return this
		val paddedArray = BooleanArray(leftPads + this.size) { i: Int ->
			val index = i - leftPads
			if(index < 0)
				false
			else
				this[index]
		}

		return Bits(paddedArray)
	}

	fun toByteArray(): ByteArray = bitsToBytes(bits).toByteArray()

	override fun toByte(): Byte = asNumber()
	override fun toChar(): kotlin.Char = toByte().toChar()
	override fun toDouble(): Double = Double.fromBits(toLong())
	override fun toFloat(): Float = Float.fromBits(toInt())
	override fun toInt(): Int = asNumber()
	override fun toShort(): Short = asNumber()

	override fun toLong(): Long
	{
		val buffer: ByteBuffer = ByteBuffer.allocate(8)
		buffer.put(toByteArray())
		buffer.flip()
		return buffer.long
	}

	inline fun <reified T: kotlin.Number> asNumber(): T
	{
		val readSize: Int = when(T::class)
		{
			Long::class, Double::class -> 8
			Int::class, Float::class -> 4
			Short::class -> 2
			Char::class, Byte::class -> 1
			else -> 0
		}

		val buffer = ByteBuffer.allocate(8)
		buffer.put(toByteArray())
		buffer.flip()

		val readFromIndex: Int = 8 - readSize
		return when(T::class)
		{
			Long::class -> buffer.getLong(readFromIndex)
			Double::class -> buffer.getDouble(readFromIndex)
			Int::class -> buffer.getInt(readFromIndex)
			Float::class -> buffer.getFloat(readFromIndex)
			Short::class -> buffer.getShort(readFromIndex)
			Char::class -> buffer.getChar(readFromIndex)
			Byte::class -> buffer.get(readFromIndex)
			else -> IllegalArgumentException("Unexpected type: ${T::class.qualifiedName}")
		} as T
	}

	fun truncate(size: Int): Bits
	{
		if(size >= this.size) return this
		val startIndex = this.size - size
		return bits.sliceArray(startIndex..bits.lastIndex).let { Bits(it) }
	}
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
			.map { (index: Int, value: Int) ->
				(value shl (rangeSize - index)).toByte()
			}
			.reduce { acc, byte -> acc or byte }

	bytes.addLast(singleByte)

	return bitsToBytes(bits, bytes, byteSequence + 1)
}

val bitMasks: List<Byte> = listOf(
		0b1000_0000,
		0b0100_0000,
		0b0010_0000,
		0b0001_0000,
		0b0000_1000,
		0b0000_0100,
		0b0000_0010,
		0b0000_0001
)
		.map { it.toByte() }

fun bitsOf(vararg booleans: Boolean): Bits = Bits(booleans.toList().toBooleanArray())

fun bitsOf(bits: List<Number>): Bits = bits
		.map { it.toByte() }
		.map { it > 0 }.toBooleanArray()
		.let { Bits(it) }

private val binaryChars: List<Char> = listOf('0', '1')

fun bitsOf(bits: String): Bits = bits.asSequence()
		.filter { it in binaryChars }
		.map { it == '1' }
		.toList()
		.toBooleanArray()
		.let { Bits(it) }

fun bitsOf(n: Int): Bits = bitsOf(n.toLong())

fun bitsOf(n: Long): Bits = n.toByteArray().let { bitsOf(it) }

fun Long.toByteArray(): ByteArray
{
	val buffer: ByteBuffer = ByteBuffer.allocate(8)
	buffer.putLong(this)
	return buffer.array()
}

fun bitsOf(bytes: ByteArray): Bits = bytes
		.map { byte ->
			bitMasks
					.map { mask: Byte ->
						byte and mask
					}
					.map { it != 0.toByte() }
		}
		.flatten()
		.toBooleanArray()
		.let { Bits(it) }
