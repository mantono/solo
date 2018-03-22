package com.mantono.solo

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CounterTest
{
	@Test
	fun testCorrectMaxEntropyFromBits()
	{
		val c4 = Counter(4)
		assertEquals(15L, c4.max)

		val c1 = Counter(1)
		assertEquals(1L, c1.max)

		val c = Counter(2)
		assertEquals(3L, c.max)
	}

	@Test
	fun testExceptionOnZeroOrNegativeBits()
	{
		assertThrows<IllegalArgumentException> { Counter(0) }
	}
}