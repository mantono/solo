package com.mantono.solo

import com.mantono.solo.api.TimestampProvider
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CounterTest
{
	@ExperimentalUnsignedTypes
	private object StaticTimeStampProvider: TimestampProvider
	{
		override fun timestamp(): ULong = 1uL
	}

	@ExperimentalUnsignedTypes
	@Test
	fun testCorrectMaxEntropyFromBits()
	{
		val c4 = Counter(4)
		assertEquals(15uL, c4.max)

		val c1 = Counter(1)
		assertEquals(1uL, c1.max)

		val c = Counter(2)
		assertEquals(3uL, c.max)
	}

	@ExperimentalUnsignedTypes
	@Test
	fun testExceptionOnZeroOrNegativeBits()
	{
		assertThrows<IllegalArgumentException> { Counter(0) }
	}

	@ExperimentalUnsignedTypes
	@Test
	fun testNoRepetitionOfSequenceOnSameTimestamp()
	{
		val c = Counter(2)
		val values: MutableList<ULong> = ArrayList()

		c.next(StaticTimeStampProvider)?.let { (ts, seq) ->
			assertEquals(1uL, ts)
			assertEquals(0uL, seq)
			values.add(seq)
		}

		c.next(StaticTimeStampProvider)?.let { (ts, seq) ->
			assertEquals(1uL, ts)
			assertEquals(1uL, seq)
			values.add(seq)
		}

		c.next(StaticTimeStampProvider)?.let { (ts, seq) ->
			assertEquals(1uL, ts)
			assertEquals(2uL, seq)
			values.add(seq)
		}

		c.next(StaticTimeStampProvider)?.let { (ts, seq) ->
			assertEquals(1uL, ts)
			assertEquals(3uL, seq)
			values.add(seq)
		}

		assertNull(c.next(StaticTimeStampProvider))
		assertEquals(4, values.size)
	}
}