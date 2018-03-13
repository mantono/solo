package com.mantono.solo.generator

import com.mantono.solo.api.Encoder
import com.mantono.solo.api.Id
import kotlinx.coroutines.experimental.channels.SendChannel

internal tailrec suspend fun <T: Id> idGenerator(
		nodeId: ByteArray,
		channel: SendChannel<T>,
		encoder: Encoder<T>,
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