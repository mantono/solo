package com.mantono.solo

import com.mantono.solo.api.Identifier
import com.mantono.solo.api.NodeIdProvider
import com.mantono.solo.encoders.FlakeIdEncoder
import com.mantono.solo.generator.IdGen
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.concurrent.TimeUnit

class IdentifierGenFlakeIdTest
{
	@ExperimentalUnsignedTypes
	@Test
	fun testBitEncoderForFlakeId()
	{
		val id = FlakeIdEncoder.encode(MillisecondsSinceUnixEpoch.timestamp(), FakeMacAddress.nodeId(), 0uL)
		assertEquals(16, id.asBytes().size)
	}

	@ExperimentalUnsignedTypes
	@Disabled
	@Test
	fun uniquenessTestCrazyLongOne()
	{
		testGenerator(10_000_000, FlakeIdEncoder, nodeIdProvider = FakeMacAddress)
	}

	@ExperimentalUnsignedTypes
	@Test
	fun uniquenessTestWithFakeMacAddressAnd128BitEncoder()
	{
		testGenerator(100_000, FlakeIdEncoder, nodeIdProvider = FakeMacAddress)
	}

	@ExperimentalUnsignedTypes
	@Test
	fun uniquenessTestWithFakeInvertedMacAddressAnd128BitEncoder()
	{
		testGenerator(100_000, FlakeIdEncoder, nodeIdProvider = FakeMacAddressInverted)
	}

	@ExperimentalUnsignedTypes
	@Test
	fun throwOnTimeoutTest()
	{
		assertThrows<TimeoutCancellationException>
		{
			runBlocking { genIdsWithShortTimeOut(10_000_000, nodeIdProvider = FakeMacAddress) }
		}
	}

	@ExperimentalUnsignedTypes
	private suspend fun genIdsWithShortTimeOut(count: Int, nodeIdProvider: NodeIdProvider): List<Identifier>
	{
		val gen = IdGen<Identifier>(10, FlakeIdEncoder, nodeId = nodeIdProvider)
		return (0 until count).map { gen.generate(1, TimeUnit.NANOSECONDS) }
				.toList()
	}
}