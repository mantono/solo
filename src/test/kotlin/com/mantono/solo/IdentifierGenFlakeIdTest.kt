package com.mantono.solo

import com.mantono.solo.api.Identifier
import com.mantono.solo.api.NodeIdProvider
import com.mantono.solo.encoders.FlakeIdEncoder
import com.mantono.solo.generator.IdGen
import com.mantono.solo.nodeid.Hostname
import com.mantono.solo.nodeid.hostname
import kotlinx.coroutines.experimental.TimeoutCancellationException
import kotlinx.coroutines.experimental.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Instant
import java.util.concurrent.TimeUnit

class IdentifierGenFlakeIdTest
{
	@Disabled
	@Test
	fun uniquenessTestCrazyLongOne()
	{
		testGenerator(10_000_000, FlakeIdEncoder, nodeIdProvider = FakeMacAddress)
	}

	@Test
	fun uniquenessTestWithFakeMacAddressAnd128BitEncoder()
	{
		testGenerator(100_000, FlakeIdEncoder, nodeIdProvider = FakeMacAddress)
	}

	@Test
	fun uniquenessTestWithFakeInvertedMacAddressAnd128BitEncoder()
	{
		testGenerator(100_000, FlakeIdEncoder, nodeIdProvider = FakeMacAddressInverted)
	}

	@Test
	fun throwOnTimeoutTest()
	{
		assertThrows<TimeoutCancellationException>
		{
			runBlocking { genIdsWithShortTimeOut(10_000_000, nodeIdProvider = FakeMacAddress) }
		}
	}

	private suspend fun genIdsWithShortTimeOut(count: Int, nodeIdProvider: NodeIdProvider): List<Identifier>
	{
		val gen = IdGen<Identifier>(10, FlakeIdEncoder, nodeId = nodeIdProvider)
		return (0 until count).map { gen.generate(1, TimeUnit.NANOSECONDS) }
				.toList()
	}
}