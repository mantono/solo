package com.mantono.solo

import com.mantono.solo.api.Identifier
import com.mantono.solo.api.NodeIdProvider
import com.mantono.solo.encoders.SnowFlakeIdEncoder
import com.mantono.solo.generator.IdGen
import com.mantono.solo.id.SnowFlakeId
import kotlinx.coroutines.experimental.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.time.Instant

class IdentifierGenSnowFlakeIdTest
{
	@Disabled
	@Test
	fun uniquenessTestCrazyLongOne()
	{
		val idsToGenerate = 5_000_000
		val start: Long = Instant.now().toEpochMilli()
		val idList: List<Identifier> = runBlocking { genIds64(idsToGenerate, FakeMacAddress) }
		val end: Long = Instant.now().toEpochMilli()
		val idSet: Set<Identifier> = idList.toSet()

		Assertions.assertEquals(idsToGenerate, idList.size)
		Assertions.assertEquals(idList.size, idSet.size)
		println("Throughput: ${idsToGenerate/(end - start).toDouble()} ids/ms")
	}

	@Test
	fun testUniquenessOnIdsCreatedOnDifferentNodesAtTheSameTime()
	{
		val fakeNodes: List<NodeIdProvider> = createFakeNodes(8)
		val generatedIds = fakeNodes.map {
			runBlocking { genIds64(500_000, nodeIdProvider = it) }
		}.flatten()

		assertEquals(generatedIds.toSet().size, generatedIds.size)
	}

	private fun createFakeNodes(nodeCount: Int): List<NodeIdProvider>
	{
		return (0 until nodeCount).map {
			object: NodeIdProvider
			{
				override fun nodeId(): ByteArray
				{
					val bytes = FakeMacAddress.nodeId()
					bytes[bytes.lastIndex] = (bytes[bytes.lastIndex] + it.toByte()).toByte()
					return bytes
				}
			}
		}.toList()
	}

	@Test
	fun uniquenessTestWithFakeMacAddressAnd64BitEncoder()
	{
		val idsToGenerate = 100_000
		val start: Long = Instant.now().toEpochMilli()
		val idList: List<Identifier> = runBlocking { genIds64(idsToGenerate, FakeMacAddress) }
		val end: Long = Instant.now().toEpochMilli()
		val idSet: Set<Identifier> = idList.toSet()

		Assertions.assertEquals(idsToGenerate, idList.size)
		Assertions.assertEquals(idList.size, idSet.size)
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

		Assertions.assertEquals(idsToGenerate, idList.size)
		Assertions.assertEquals(idList.size, idSet.size)
		println("Throughput: ${idsToGenerate/(end - start).toDouble()} ids/ms")
	}

	private suspend fun genIds64(count: Int, nodeIdProvider: NodeIdProvider, buffer: Int = 1000, waitTimeMs: Long = 1000): List<SnowFlakeId>
	{
		val gen = IdGen(buffer, SnowFlakeIdEncoder, nodeId = nodeIdProvider)
		return (0 until count).map { gen.generate(waitTimeMs) }
				.toList()
	}
}