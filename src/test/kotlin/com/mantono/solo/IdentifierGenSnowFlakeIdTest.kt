package com.mantono.solo

import com.mantono.solo.api.NodeIdProvider
import com.mantono.solo.encoders.SnowFlakeIdEncoder
import com.mantono.solo.generator.IdGen
import com.mantono.solo.id.SnowFlakeId
import kotlinx.coroutines.experimental.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class IdentifierGenSnowFlakeIdTest
{
	@Disabled
	@Test
	fun uniquenessTestCrazyLongOne()
	{
		testGenerator(10_000_000, SnowFlakeIdEncoder, nodeIdProvider = FakeMacAddress)
	}

	@Test
	fun testUniquenessOnIdsCreatedOnDifferentNodesAtTheSameTime()
	{
		val fakeNodes: List<NodeIdProvider> = createFakeNodes(8)
		val generatedIds = fakeNodes.map {
			runBlocking { testGenerator(500_000, nodeIdProvider = it, encoder = SnowFlakeIdEncoder) }
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
		testGenerator(100_000, SnowFlakeIdEncoder, nodeIdProvider = FakeMacAddress)
	}

	@Test
	fun uniquenessTestWithFakeInvertedMacAddressAnd64BitEncoder()
	{
		testGenerator(100_000, SnowFlakeIdEncoder, nodeIdProvider = FakeMacAddressInverted)
	}

	@Test
	fun testAsLong()
	{
		val generator = IdGen<SnowFlakeId>(10, SnowFlakeIdEncoder, MillisecondsSinceUnixEpoch)
		val id: Long = runBlocking { generator.generate().asLong() }
		assertNotEquals(0L, id)
	}
}