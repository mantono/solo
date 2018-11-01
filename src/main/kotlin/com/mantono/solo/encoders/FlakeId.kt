package com.mantono.solo.encoders

import com.mantono.solo.id.FlakeId

object FlakeIdEncoder: BitEncoder<FlakeId>(64, 48, 16)
{
	override fun encode(timestamp: ULong, nodeId: ByteArray, sequence: ULong): FlakeId
	{
		return FlakeId(generateByteArray(timestamp, nodeId, sequence))
	}
}