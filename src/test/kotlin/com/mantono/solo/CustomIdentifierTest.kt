package com.mantono.solo

import com.mantono.solo.api.Encoder
import com.mantono.solo.api.Identifier
import com.mantono.solo.api.NodeIdProvider
import com.mantono.solo.encoders.BitEncoder
import com.mantono.solo.generator.IdGen
import kotlinx.coroutines.experimental.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CustomIdentifierTest
{
	@Test
	fun testUniquesInEncoderWithLowSequenceEntropy()
	{
		val ids: List<Identifier> = genCustomId(1_000, FakeMacAddress, EncoderWithShortSequence, 50)
		val asSet: Set<Identifier> = ids.toSet()
		ids.forEach { println(it.asString()) }
		assertEquals(ids.size, asSet.size)
	}
}

private fun <T: Identifier> genCustomId(count: Int, nodeIdProvider: NodeIdProvider, encoder: Encoder<T>, buffer: Int = 1000, waitTimeMs: Long = 1000): List<T>
{
	val gen = IdGen(buffer, encoder, nodeId = nodeIdProvider)
	return runBlocking {
		(0 until count).map { gen.generate(waitTimeMs) }
				.toList()
	}
}

object EncoderWithShortSequence: BitEncoder<Identifier>(36, 24, 4)
{
	override fun encode(timestamp: Long, nodeId: ByteArray, sequence: Long): Identifier
	{
		val bytes = this.generateByteArray(timestamp, nodeId, sequence)
		return object: Identifier
		{
			override fun asBytes(): ByteArray = bytes
		}
	}

}