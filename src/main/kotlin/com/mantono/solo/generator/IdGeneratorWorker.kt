package com.mantono.solo.generator

import com.mantono.solo.api.Encoder
import com.mantono.solo.api.Identifier
import com.mantono.solo.api.SequenceCounter
import com.mantono.solo.api.TimestampProvider
import kotlinx.coroutines.experimental.channels.SendChannel

internal tailrec suspend fun <T: Identifier> idGenerator(
		nodeId: ByteArray,
		channel: SendChannel<T>,
		encoder: Encoder<T>,
		sequence: SequenceCounter,
		timestampProvider: TimestampProvider
)
{
	sequence.next(timestampProvider)?.let { (timestamp, seq) ->
		val id: T = encoder(timestamp, nodeId, seq)
		channel.send(id)
	}

	idGenerator(nodeId, channel, encoder, sequence, timestampProvider)
}