package com.mantono.solo.encoders

import com.mantono.solo.id.FlakeId

object FlakeIdEncoder : BitEncoder<FlakeId>(64, 48, 16) {
    override fun encode(timestamp: Long, nodeId: ByteArray, sequence: Long): FlakeId =
        FlakeId(generateByteArray(timestamp, nodeId, sequence))
}