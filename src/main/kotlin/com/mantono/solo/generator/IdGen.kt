package com.mantono.solo.generator

import com.mantono.solo.Counter
import com.mantono.solo.nodeid.MacAddress
import com.mantono.solo.MillisecondsSinceUnixEpoch
import com.mantono.solo.api.Encoder
import com.mantono.solo.api.IdGenerator
import com.mantono.solo.api.Identifier
import com.mantono.solo.api.NodeIdProvider
import com.mantono.solo.api.SequenceCounter
import com.mantono.solo.api.TimestampProvider
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
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

	override suspend fun generate(maxWaitTime: Long, unit: TimeUnit): T = idChannel.receive(maxWaitTime, unit)
}

private suspend fun <E> ReceiveChannel<E>.receive(time: Long, unit: TimeUnit = TimeUnit.MILLISECONDS): E
{
	val rc = this
	return withTimeout(time, unit) { rc.receive() }
}