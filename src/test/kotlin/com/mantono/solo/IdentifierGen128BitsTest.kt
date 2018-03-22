package com.mantono.solo

import com.mantono.solo.api.Identifier
import com.mantono.solo.api.NodeIdProvider
import com.mantono.solo.generator.IdGen
import kotlinx.coroutines.experimental.TimeoutCancellationException
import kotlinx.coroutines.experimental.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Instant
import kotlin.experimental.inv

class IdentifierGen128BitsTest
{
	private object FakeMacAddress: NodeIdProvider
	{
		override fun nodeId(): ByteArray = byteArrayOf(
				0x96.toByte(),
				0xb6.toByte(),
				0xd0.toByte(),
				0xd8.toByte(),
				0xda.toByte(),
				0x6d.toByte()
		)
	}

	private object FakeMacAddressInverted: NodeIdProvider
	{
		override fun nodeId(): ByteArray = byteArrayOf(
				0x96.toByte().inv(),
				0xb6.toByte(),
				0xd0.toByte(),
				0xd8.toByte(),
				0xda.toByte(),
				0x6d.toByte()
		)
	}


	@Disabled
	@Test
	fun uniquenessTestCrazyLongOne()
	{
		val idsToGenerate = 5_000_000
		val start: Long = Instant.now().toEpochMilli()
		val idList: List<Identifier> = runBlocking { genIds64(idsToGenerate, FakeMacAddress) }
		val end: Long = Instant.now().toEpochMilli()
		val idSet: Set<Identifier> = idList.toSet()

		assertEquals(idsToGenerate, idList.size)
		assertEquals(idList.size, idSet.size)
		println("Throughput: ${idsToGenerate/(end - start).toDouble()} ids/ms")
	}

	@Test
	fun uniquenessTestWithFakeMacAddressAnd64BitEncoder()
	{
		val idsToGenerate = 100_000
		val start: Long = Instant.now().toEpochMilli()
		val idList: List<Identifier> = runBlocking { genIds64(idsToGenerate, FakeMacAddress) }
		val end: Long = Instant.now().toEpochMilli()
		val idSet: Set<Identifier> = idList.toSet()

		assertEquals(idsToGenerate, idList.size)
		assertEquals(idList.size, idSet.size)
		println("Throughput: ${idsToGenerate/(end - start).toDouble()} ids/ms")
	}

	@Test
	fun uniquenessTestWithFakeInvertedMacAddressAnd64BitEncoder()
	{
		val idsToGenerate = 100_000
		val start: Long = Instant.now().toEpochMilli()
		val idList: List<Identifier> = runBlocking { genIds64(idsToGenerate, FakeMacAddressInverted) }
		val end: Long = Instant.now().toEpochMilli()
		val idSet: Set<Identifier> = idList.toSet()

		assertEquals(idsToGenerate, idList.size)
		assertEquals(idList.size, idSet.size)
		println("Throughput: ${idsToGenerate/(end - start).toDouble()} ids/ms")
	}

	private suspend fun genIds64(count: Int, nodeIdProvider: NodeIdProvider): List<Identifier>
	{
		val gen = IdGen<Identifier>(1000, Default64BitEncoder, nodeId = nodeIdProvider)
		return (0 until count).map { gen.generate(1000) }
				.toList()
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

	private suspend fun genIds128(count: Int, nodeIdProvider: NodeIdProvider): List<Identifier>
	{
		val gen = IdGen<Identifier>(1000, Default128BitEncoder, nodeId = nodeIdProvider)
		return (0 until count).map { gen.generate(1000) }
				.toList()
	}

	@Test
	fun throwOnTimeoutTest()
	{
		assertThrows<TimeoutCancellationException>
		{
			runBlocking { genIdsWithShortTimeOut(100_000, nodeIdProvider = FakeMacAddress) }
		}
	}

	private suspend fun genIdsWithShortTimeOut(count: Int, nodeIdProvider: NodeIdProvider): List<Identifier>
	{
		val gen = IdGen<Identifier>(10, Default128BitEncoder, nodeId = nodeIdProvider)
		return (0 until count).map { gen.generate(1) }
				.toList()
	}
}