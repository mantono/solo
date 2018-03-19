package com.mantono.solo.generator

import com.mantono.solo.Counter
import com.mantono.solo.MillisecondsSinceUnixEpoch
import com.mantono.solo.api.Encoder
import com.mantono.solo.api.Identifier
import com.mantono.solo.api.IdGenerator
import com.mantono.solo.getMacAddress
import kotlinx.coroutines.experimental.TimeoutCancellationException
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import java.util.concurrent.TimeUnit

class IdGen<out T: Identifier>(buffer: Int = 1000, encoder: Encoder<T>): IdGenerator<T>
{
	override val nodeId: ByteArray = getMacAddress()

	private val idChannel: Channel<T> = Channel(buffer)

	init
	{
		launch { idGenerator(nodeId, idChannel, encoder, Counter(1000), MillisecondsSinceUnixEpoch) }
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