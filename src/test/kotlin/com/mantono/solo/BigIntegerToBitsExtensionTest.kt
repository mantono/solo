package com.mantono.solo

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigInteger

class BigIntegerToBitsExtensionTest
{
	@Test
	fun testBigIntegerToBitsOne()
	{
		assertEquals("00000001", BigInteger.ONE.toBitsString())
	}

	@Test
	fun testBigIntegerToBitsZero()
	{
		assertEquals("00000000", BigInteger.ZERO.toBitsString())
	}

	@Test
	fun testBigIntegerToBitsTen()
	{
		assertEquals("00001010", BigInteger.TEN.toBitsString())
	}

	@Test
	fun testBigIntegerToBitsNegativeOne()
	{
		assertEquals("11111110", BigInteger.valueOf(-1L).toBitsString())
	}

	@Test
	fun testBigIntegerToBitsSeveralBytes()
	{
		assertEquals("00000100 00000000", BigInteger.valueOf(1024L).toBitsString())
	}

	@Test
	fun testBigIntegerToBitsSeveralBytesNegative()
	{
		assertEquals("11111011 11111111", BigInteger.valueOf(-1024L).toBitsString())
	}
}