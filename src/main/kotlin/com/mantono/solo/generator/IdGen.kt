package com.mantono.solo.generator

import com.mantono.solo.api.Encoder
import com.mantono.solo.api.Id
import com.mantono.solo.api.IdGenerator
import com.mantono.solo.getMacAddress
import kotlinx.coroutines.experimental.TimeoutCancellationException
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import java.util.concurrent.TimeUnit

class IdGen<out T: Id>(buffer: Int = 1000, encoder: Encoder<T>): IdGenerator<T>
{
	override val nodeId: ByteArray = getMacAddress()

	private val idChannel: Channel<T> = Channel(buffer)

	init
	{
		val id1: T = encoder(byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0), 0, Long.MAX_VALUE)
		val id2: T = encoder(byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0), 0, Long.MIN_VALUE)
		launch { idGenerator(nodeId, idChannel, encoder, Counter(1000), InstantEpochMs) }
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