package com.mantono.solo.generator

import com.mantono.solo.api.Id
import kotlinx.coroutines.experimental.channels.SendChannel


typealias Encoder<T> = (ByteArray, Long, Long) -> T

internal tailrec suspend fun <T: Id> idGenerator(
		nodeId: ByteArray,
		channel: SendChannel<T>,
		encoder: (ByteArray, Long, Long) -> T,
		sequence: SequenceCounter,
		timestampProvider: TimestampProvider = InstantEpochMs
)
{
	sequence.next(timestampProvider)?.let { (timestamp, seq) ->
		val id: T = encoder(nodeId, timestamp, seq)
		channel.send(id)
	}

	idGenerator(nodeId, channel, encoder, sequence, timestampProvider)
}