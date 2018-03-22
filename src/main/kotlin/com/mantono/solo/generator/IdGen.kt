package com.mantono.solo.generator

import com.mantono.solo.Counter
import com.mantono.solo.MacAddress
import com.mantono.solo.MillisecondsSinceUnixEpoch
import com.mantono.solo.api.Encoder
import com.mantono.solo.api.Identifier
import com.mantono.solo.api.IdGenerator
import com.mantono.solo.api.NodeIdProvider
import com.mantono.solo.api.SequenceCounter
import com.mantono.solo.api.TimestampProvider
import kotlinx.coroutines.experimental.TimeoutCancellationException
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import java.util.concurrent.TimeUnit

class IdGen<out T: Identifier>(
		buffer: Int = 1000,
		encoder: Encoder<T>,
		timestamp: TimestampProvider = MillisecondsSinceUnixEpoch,
		nodeId: NodeIdProvider = MacAddress,
		counter: SequenceCounter = Counter(encoder.sequenceBits)
): IdGenerator<T>
{
	private val idChannel: Channel<T> = Channel(buffer)

	init
	{
		launch { idGenerator(nodeId.nodeId(), idChannel, encoder, counter, timestamp) }
	}

	override suspend fun generate(maxWaitTime: Long): T
	{
		val throwException = async {
			delay(maxWaitTime, TimeUnit.MILLISECONDS)
			throw TimeoutCancellationException("$maxWaitTime milliseconds passed without getting an ID")
		}
		val id: T = idChannel.receive()
		throwException.cancel()
		return id
	}
}