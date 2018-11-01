package com.mantono.solo

import com.mantono.solo.api.TimestampProvider
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CounterTest {
    private object StaticTimeStampProvider : TimestampProvider {
        override fun timestamp(): Long = 1L
    }

    @Test
    fun testCorrectMaxEntropyFromBits() {
        val c4 = Counter(4)
        assertEquals(15L, c4.max)

        val c1 = Counter(1)
        assertEquals(1L, c1.max)

        val c = Counter(2)
        assertEquals(3L, c.max)
    }

    @Test
    fun testExceptionOnZeroOrNegativeBits() {
        assertThrows<IllegalArgumentException> { Counter(0) }
    }

    @Test
    fun testNoRepetitionOfSequenceOnSameTimestamp() {
        val c = Counter(2)
        val values: MutableList<Long> = ArrayList()

        c.next(StaticTimeStampProvider)?.let { (ts, seq) ->
            assertEquals(1L, ts)
            assertEquals(0, seq)
            values.add(seq)
        }

        c.next(StaticTimeStampProvider)?.let { (ts, seq) ->
            assertEquals(1L, ts)
            assertEquals(1, seq)
            values.add(seq)
        }

        c.next(StaticTimeStampProvider)?.let { (ts, seq) ->
            assertEquals(1L, ts)
            assertEquals(2, seq)
            values.add(seq)
        }

        c.next(StaticTimeStampProvider)?.let { (ts, seq) ->
            assertEquals(1L, ts)
            assertEquals(3, seq)
            values.add(seq)
        }

        assertNull(c.next(StaticTimeStampProvider))
        assertEquals(4, values.size)
    }
}