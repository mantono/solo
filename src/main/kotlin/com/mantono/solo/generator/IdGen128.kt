package com.mantono.solo.generator

import com.mantono.solo.BitEncoder
import com.mantono.solo.api.Id128
import com.mantono.solo.api.IdGenerator128
import com.mantono.solo.getMacAddress
import com.mantono.solo.id.Id128Bits
import kotlinx.coroutines.experimental.TimeoutCancellationException
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import java.time.Instant
import java.util.concurrent.TimeUnit


class IdGen128(buffer: Int = 1000, parallelism: Int = 1): IdGenerator128
{
	override val nodeId: ByteArray = getMacAddress()

	private val idChannel: Channel<Id128> = Channel(buffer)

	init
	{
		launch { idGenerator(nodeId, idChannel) }
	}

	override suspend fun generate(maxWaitTime: Long): Id128
	{
		val throwException = async {
			delay(maxWaitTime, TimeUnit.MILLISECONDS)
			throw TimeoutCancellationException("$maxWaitTime milliseconds passed without getting an ID")
		}
		val id: Id128 = idChannel.receive()
		throwException.cancel()
		return id
	}
}

private val encoder = BitEncoder(64, 48, 16)

internal tailrec suspend fun idGenerator(
		nodeId: ByteArray,
		channel: SendChannel<Id128>,
		lastGenerated: Long = Instant.now().toEpochMilli(),
		sequence: Long = 0
)
{
	val now: Long = epochMs()
	if(sequence < encoder.maxSequenceValue)
	{
		val idBytes: ByteArray = encoder.generate(nodeId, now, sequence)
		channel.send(Id128Bits(idBytes))
	}

	when
	{
		lastGenerated == now -> idGenerator(nodeId, channel, now, sequence + 1)
		lastGenerated < now -> idGenerator(nodeId, channel, now, 0)
		else -> throw IllegalStateException("Unhandled time shift occurred")
	}
}

private fun epochMs(): Long = Instant.now().toEpochMilli()