package com.mantono.solo

import com.mantono.solo.api.Identifier
import com.mantono.solo.api.NodeIdProvider
import com.mantono.solo.encoders.FlakeIdEncoder
import com.mantono.solo.generator.IdGen
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
		val idsToGenerate = 5_000_000
		val start: Long = Instant.now().toEpochMilli()
		val idList: List<Identifier> = runBlocking { genIds128(idsToGenerate, FakeMacAddress) }
		val end: Long = Instant.now().toEpochMilli()
		val idSet: Set<Identifier> = idList.toSet()

		assertEquals(idsToGenerate, idList.size)
		assertEquals(idList.size, idSet.size)
		println("Throughput: ${idsToGenerate/(end - start).toDouble()} ids/ms")
	}

	@Test
	fun uniquenessTestWithFakeMacAddressAnd128BitEncoder()
	{
		val idsToGenerate = 100_000
		val start: Long = Instant.now().toEpochMilli()
		val idList: List<Identifier> = runBlocking { genIds128(idsToGenerate, FakeMacAddress) }
		val end: Long = Instant.now().toEpochMilli()
		val idSet: Set<Identifier> = idList.toSet()

		assertEquals(idsToGenerate, idList.size)
		assertEquals(idList.size, idSet.size)
		println("Throughput: ${idsToGenerate/(end - start).toDouble()} ids/ms")
	}

	@Test
	fun uniquenessTestWithFakeInvertedMacAddressAnd128BitEncoder()
	{
		val idsToGenerate = 100_000
		val start: Long = Instant.now().toEpochMilli()
		val idList: List<Identifier> = runBlocking { genIds128(idsToGenerate, FakeMacAddressInverted) }
		val end: Long = Instant.now().toEpochMilli()
		val idSet: Set<Identifier> = idList.toSet()

		assertEquals(idsToGenerate, idList.size)
		assertEquals(idList.size, idSet.size)
		println("Throughput: ${idsToGenerate/(end - start).toDouble()} ids/ms")
	}

	private suspend fun genIds128(count: Int, nodeIdProvider: NodeIdProvider, buffer: Int = 1000, waitTimeMs: Long = 1000): List<Identifier>
	{
		val gen = IdGen<Identifier>(buffer, FlakeIdEncoder, nodeId = nodeIdProvider)
		return (0 until count).map { gen.generate(waitTimeMs) }
				.toList()
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