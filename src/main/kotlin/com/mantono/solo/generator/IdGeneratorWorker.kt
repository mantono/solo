package com.mantono.solo.generator

import com.mantono.solo.BitEncoder
import com.mantono.solo.api.Id
import com.mantono.solo.id.Id128Bits
import com.mantono.solo.id.Id64Bits
import kotlinx.coroutines.experimental.channels.SendChannel
import java.time.Instant


typealias Encoder<T> = (ByteArray, Long, Long) -> T

internal tailrec suspend fun <T: Id> idGenerator(
		nodeId: ByteArray,
		channel: SendChannel<T>,
		encoder: Encoder<T>,
		timestampProvider: TimestampProvider = InstantEpochMs,
		sequence: SequenceCounter
)
{
	sequence.next(timestampProvider)?.let { (timestamp, seq) ->
		val id: T = encoder(nodeId, timestamp, seq)
		channel.send(id)
	}

	idGenerator(nodeId, channel, encoder, timestampProvider, sequence)
}