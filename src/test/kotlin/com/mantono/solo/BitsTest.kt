package com.mantono.solo

import com.mantono.solo.bits.Bits
import com.mantono.solo.bits.bitsOf
import com.mantono.solo.bits.toByteArray
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class BitsTest
{
	private val booleans11 = booleanArrayOf(true, false, true, true)

	// 0111 1011 --> 64 + 32 + 16 + 8 + 2 + 1 = 123
	private val booleans123 = booleanArrayOf(false, true, true, true, true, false, true, true)

	@Test
	fun testBitsToByteArrayOnHalfByte()
	{
		val bits: Bits = Bits(booleans11)

		val byte: ByteArray = bits.toByteArray()
		assertEquals(1, byte.size)
		assertEquals(11, byte.first())
	}

	@Test
	fun testBitsToByteArrayOnFullByte()
	{
		val bits: Bits = Bits(booleans123)
		val byte: ByteArray = bits.toByteArray()
		assertEquals(1, byte.size)
		assertEquals(123, byte.first())
	}


	@Test
	fun testBitsToByteArrayOnTwoBytes()
	{
		val bits = bitsOf(listOf(
				0, 1, 1, 1,	1, 0, 1, 1,
				0, 1, 0, 1,	0, 1, 1, 1
				))

		val byte: ByteArray = bits.toByteArray()
		assertEquals(2, byte.size)
		assertEquals(123, byte.first())
	}

	@Test
	fun testLongCreatorWith1()
	{
		val b = bitsOf(1L)
		assertEquals(1L, b.toLong())
	}

	@Test
	fun testLongCreatorWith15()
	{
		val b = bitsOf(16L)
		assertEquals(16L, b.toLong())
	}
	@Test
	fun testLongCreatorWith128()
	{
		val b = bitsOf(128L)
		assertEquals(128L, b.toLong())
	}

	@Test
	fun testLongToByteArrayWith16()
	{
		val bytes: ByteArray = 16L.toByteArray()
		val expected: ByteArray = listOf(
				0, 0, 0, 0, 0, 0, 0, 16
		)
				.map { it.toByte() }
				.toByteArray()

		assertArrayEquals(expected, bytes, printArray(bytes))
	}

	@Test
	fun testLongToByteArray()
	{
		val bytes: ByteArray = 15L.toByteArray()
		val expected: ByteArray = listOf(
				0, 0, 0, 0, 0, 0, 0, 15
		)
				.map { it.toByte() }
				.toByteArray()

		assertArrayEquals(expected, bytes)
	}

	@Test
	fun testIntToByteArrayToInt()
	{
		val i32: Int = bitsOf(32).toInt()
		assertEquals(32, i32)
	}

	@Test
	fun testIntToByteArrayToShort()
	{
		val s32: Short = bitsOf(32).toShort()
		assertEquals(32.toShort(), s32)
	}

	@Test
	fun testIntToByteArrayToShortOnSeveralBytes()
	{
		val s256: Short = bitsOf(256).toShort()
		assertEquals(256.toShort(), s256)
	}

	@Test
	fun testLongToByteArrayToByteWithinBounds()
	{
		val b32: Byte = bitsOf(32L).toByte()
		assertEquals(32.toByte(), b32)
	}

	@Test
	fun testLongToByteArrayToByteWithOverflow()
	{
		// 1011 0000 0110 = 2048 + 512 + 256 + 4 + 2 = 2822
		// 1011 is outside byte range, so expected is 0000 0110 which equals to 6
		val b6: Byte = bitsOf(2822).toByte()
		assertEquals(6.toByte(), b6)
	}

	private fun printArray(arr: ByteArray) = arr
			.mapIndexed { index, byte -> index to byte }
			.joinToString(separator = " ") { (index: Int, byte: Byte) ->
				when{
					index % 7 == 0 -> "$byte\n"
					index % 4 == 0 -> "_${byte}"
					else -> byte.toString()
				}
			}
}