package com.mantono.solo

import com.mantono.pyttipanna.hashing.HashAlgorithm
import com.mantono.pyttipanna.hashing.hash
import com.mantono.solo.api.Encoder
import com.mantono.solo.api.IdGenerator
import com.mantono.solo.api.Identifier
import com.mantono.solo.api.NodeIdProvider
import com.mantono.solo.api.TimestampProvider
import com.mantono.solo.generator.IdGen
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import java.time.Instant
import kotlin.experimental.inv

internal object FakeMacAddress: NodeIdProvider
{
	override fun nodeId(): ByteArray = byteArrayOf(
			0x96.toByte(),
			0xb6.toByte(),
			0xd0.toByte(),
			0xd8.toByte(),
			0xda.toByte(),
			0x6d.toByte()
	).let { hash(it, algorithm = HashAlgorithm.SHA256) }
}

internal object FakeMacAddressInverted: NodeIdProvider
{
	override fun nodeId(): ByteArray = byteArrayOf(
			0x96.toByte().inv(),
			0xb6.toByte(),
			0xd0.toByte(),
			0xd8.toByte(),
			0xda.toByte(),
			0x6d.toByte()
	).let { hash(it, algorithm = HashAlgorithm.SHA256) }
}

fun <T: Identifier> testGenerator(
		count: Int,
		encoder: Encoder<T>,
		timestampProvider: TimestampProvider = MillisecondsSinceUnixEpoch,
		nodeIdProvider: NodeIdProvider = FakeMacAddress,
		buffer: Int = 1000,
		waitTimeMs: Long = 1000
): List<Identifier>
{
	val start: Long = Instant.now().toEpochMilli()
	val generator: IdGenerator<T> = IdGen(buffer, encoder, timestampProvider, nodeIdProvider)
	val idList: List<Identifier> = runBlocking { (0 until count).map { generator.generate(waitTimeMs) } }
	val end: Long = Instant.now().toEpochMilli()
	val idSet: Set<Identifier> = idList.toSet()

	assertEquals(count, idList.size)
	assertEquals(idList.size, idSet.size) { idList.joinToString(separator = "\n") { it.asString() } }
	println("Throughput: ${count/(end - start).toDouble()} ids/ms")

	return idList
}