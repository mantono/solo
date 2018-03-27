package com.mantono.solo

import com.mantono.solo.api.Identifier
import com.mantono.solo.encoders.BitEncoder
import org.junit.jupiter.api.Test

class CustomIdentifierTest
{
	@Test
	fun testUniquesInEncoderWithLowSequenceEntropy()
	{
		testGenerator(1_000, EncoderWithShortSequence, nodeIdProvider = FakeMacAddress, buffer = 50)
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