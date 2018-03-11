package com.mantono.solo

import com.mantono.solo.bits.Bits
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class BitsTest
{
	@Test
	fun testBitsToByteArrayOnHalfByte()
	{
		// 0000 1011 --> 8 + 2 + 1 = 11
		val bools = booleanArrayOf(true, false, true, true)
		val bits: Bits = Bits(bools)

		val byte: ByteArray = bits.toByteArray()
		assertEquals(1, byte.size)
		assertEquals(11, byte.first())
	}

	@Test
	fun testBitsToByteArrayOnFullByte()
	{
		// 0111 1011 --> 64 + 32 + 16 + 8 + 2 + 1 = 123
		val bools = booleanArrayOf(false, true, true, true, true, false, true, true)
		val bits: Bits = Bits(bools)

		val byte: ByteArray = bits.toByteArray()
		assertEquals(1, byte.size)
		assertEquals(123, byte.first())
	}


	@Test
	fun testBitsToByteArrayOnTwoBytes()
	{
		// 0111 1011 --> 64 + 32 + 16 + 8 + 2 + 1 = 123
		val bools = listOf(
				0, 1, 1, 1,	1, 0, 1, 1,
				0, 1, 0, 1,	0, 1, 1, 1
				)
		val bits: Bits = Bits(bools)

		val byte: ByteArray = bits.toByteArray()
		assertEquals(2, byte.size)
		assertEquals(123, byte.first())
	}
}