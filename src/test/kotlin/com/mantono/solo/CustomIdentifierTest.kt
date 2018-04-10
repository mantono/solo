package com.mantono.solo

import com.mantono.solo.api.Identifier
import com.mantono.solo.encoders.BitEncoder
import org.junit.jupiter.api.Test
import java.util.*

class CustomIdentifierTest
{
	@Test
	fun testUniquesInEncoderWithLowSequenceEntropy()
	{
		testGenerator(1_000, EncoderWithShortSequence, nodeIdProvider = FakeMacAddress, buffer = 50, waitTimeMs = 300_000).forEach {
			println(it.asString())
		}
	}
}

object EncoderWithShortSequence: BitEncoder<Identifier>(38, 24, 2)
{
	override fun encode(timestamp: Long, nodeId: ByteArray, sequence: Long): Identifier
	{
		val bytes = this.generateByteArray(timestamp, nodeId, sequence)
		return object: Identifier
		{
			override fun asBytes(): ByteArray = bytes

			override fun equals(other: Any?): Boolean
			{
				if(this === other) return true
				if(other !is Identifier) return false

				return bytes.contentEquals(other.asBytes())
			}

			override fun hashCode(): Int = Arrays.hashCode(bytes)

			override fun toString(): String = asString()
		}
	}
}