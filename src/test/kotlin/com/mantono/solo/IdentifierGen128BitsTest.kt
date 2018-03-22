package com.mantono.solo

import com.mantono.solo.api.Identifier
import com.mantono.solo.api.NodeIdProvider
import com.mantono.solo.generator.IdGen
import kotlinx.coroutines.experimental.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
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

	@Test
	fun uniquenessTestWithFakeMacAddress()
	{
		val idsToGenerate = 100_000
		val start: Long = Instant.now().toEpochMilli()
		val idList: List<Identifier> = runBlocking { genIds(idsToGenerate, FakeMacAddress) }
		val end: Long = Instant.now().toEpochMilli()
		val idSet: Set<Identifier> = idList.toSet()

		assertEquals(idsToGenerate, idList.size)
		assertEquals(idList.size, idSet.size)
		println("Throughput: ${idsToGenerate/(end - start).toDouble()} ids/ms")
	}

	@Test
	fun uniquenessTestWithFakeInvertedMacAddress()
	{
		val idsToGenerate = 100_000
		val start: Long = Instant.now().toEpochMilli()
		val idList: List<Identifier> = runBlocking { genIds(idsToGenerate, FakeMacAddressInverted) }
		val end: Long = Instant.now().toEpochMilli()
		val idSet: Set<Identifier> = idList.toSet()

		assertEquals(idsToGenerate, idList.size)
		assertEquals(idList.size, idSet.size)
		println("Throughput: ${idsToGenerate/(end - start).toDouble()} ids/ms")
	}

	private suspend fun genIds(count: Int, nodeIdProvider: NodeIdProvider): List<Identifier>
	{
		val gen = IdGen<Identifier>(1000, Default64BitEncoder, nodeId = nodeIdProvider)
		return (0 until count).map { gen.generate(1000) }
				.onEach { println(it.asString()) }
				.toList()
	}
}