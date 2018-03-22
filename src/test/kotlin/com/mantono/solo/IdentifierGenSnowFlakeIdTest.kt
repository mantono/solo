package com.mantono.solo

import com.mantono.solo.api.Identifier
import com.mantono.solo.api.NodeIdProvider
import com.mantono.solo.encoders.FlakeIdEncoder
import com.mantono.solo.encoders.SnoFlakeIdEncoder
import com.mantono.solo.generator.IdGen
import kotlinx.coroutines.experimental.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.time.Instant

class IdentifierGenSnowFlakeIdTest
{
	@Disabled
	@Test
	fun uniquenessTestCrazyLongOne()
	{
		val idsToGenerate = 5_000_000
		val start: Long = Instant.now().toEpochMilli()
		val idList: List<Identifier> = runBlocking { genIds64(idsToGenerate, FakeMacAddress) }
		val end: Long = Instant.now().toEpochMilli()
		val idSet: Set<Identifier> = idList.toSet()

		Assertions.assertEquals(idsToGenerate, idList.size)
		Assertions.assertEquals(idList.size, idSet.size)
		println("Throughput: ${idsToGenerate/(end - start).toDouble()} ids/ms")
	}

	@Test
	fun uniquenessTestWithFakeMacAddressAnd64BitEncoder()
	{
		val idsToGenerate = 100_000
		val start: Long = Instant.now().toEpochMilli()
		val idList: List<Identifier> = runBlocking { genIds64(idsToGenerate, FakeMacAddress) }
		val end: Long = Instant.now().toEpochMilli()
		val idSet: Set<Identifier> = idList.toSet()

		Assertions.assertEquals(idsToGenerate, idList.size)
		Assertions.assertEquals(idList.size, idSet.size)
		println("Throughput: ${idsToGenerate/(end - start).toDouble()} ids/ms")
	}

	@Test
	fun uniquenessTestWithFakeInvertedMacAddressAnd64BitEncoder()
	{
		val idsToGenerate = 100_000
		val start: Long = Instant.now().toEpochMilli()
		val idList: List<Identifier> = runBlocking { genIds64(idsToGenerate, FakeMacAddressInverted) }
		val end: Long = Instant.now().toEpochMilli()
		val idSet: Set<Identifier> = idList.toSet()

		Assertions.assertEquals(idsToGenerate, idList.size)
		Assertions.assertEquals(idList.size, idSet.size)
		println("Throughput: ${idsToGenerate/(end - start).toDouble()} ids/ms")
	}

	private suspend fun genIds64(count: Int, nodeIdProvider: NodeIdProvider): List<Identifier>
	{
		val gen = IdGen<Identifier>(1000, SnoFlakeIdEncoder, nodeId = nodeIdProvider)
		return (0 until count).map { gen.generate(1000) }
				.toList()
	}
}