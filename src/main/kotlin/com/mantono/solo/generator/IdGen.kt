package com.mantono.solo.generator

import com.mantono.solo.Bit64Encoder
import com.mantono.solo.BitEncoder
import com.mantono.solo.api.Id
import com.mantono.solo.api.Id64
import com.mantono.solo.api.IdGenerator
import com.mantono.solo.getMacAddress
import com.mantono.solo.id.Id64Bits
import kotlinx.coroutines.experimental.TimeoutCancellationException
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import java.util.concurrent.TimeUnit

val encoder = Bit64Encoder(64, 48, 16)

object id64enc: (ByteArray, Long, Long) -> Id64
{

	override fun invoke(p1: ByteArray, p2: Long, p3: Long): Id64
	{
		return Id64Bits(encoder.generateByteArray(p1, p2, p3))
	}

}



class IdGen<out T: Id>(buffer: Int = 1000, parallelism: Int = 1, encoder: Encoder<T>): IdGenerator<T>
{
	override val nodeId: ByteArray = getMacAddress()

	private val idChannel: Channel<T> = Channel(buffer)

	init
	{
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