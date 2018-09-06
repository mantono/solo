package com.mantono.solo.encoders

import com.mantono.solo.id.SnowFlakeId

@ExperimentalUnsignedTypes
object SnowFlakeIdEncoder: BitEncoder<SnowFlakeId>(42, 12, 10)
{
	override fun encode(timestamp: ULong, nodeId: ByteArray, sequence: ULong): SnowFlakeId
	{
		return SnowFlakeId(generateByteArray(timestamp, nodeId, sequence))
	}
}