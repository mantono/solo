package com.mantono.solo.encoders

import com.mantono.solo.id.SnowFlakeId

object SnoFlakeIdEncoder: BitEncoder<SnowFlakeId>(42, 12, 10)
{
	override fun encode(timestamp: Long, nodeId: ByteArray, sequence: Long): SnowFlakeId
	{
		return SnowFlakeId(generateByteArray(timestamp, nodeId, sequence))
	}
}