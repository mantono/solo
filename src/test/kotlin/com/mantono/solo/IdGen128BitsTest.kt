package com.mantono.solo

import com.mantono.solo.api.Id128
import kotlinx.coroutines.experimental.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Instant

class IdGen128BitsTest
{
	@Test
	fun uniquenessTest()
	{
		val idsToGenerate = 1_000
		val start: Long = Instant.now().toEpochMilli()
		val idList: List<Id128> = runBlocking { genIds(idsToGenerate) }
		val end: Long = Instant.now().toEpochMilli()
		val idSet: Set<Id128> = idList.toSet()

		assertEquals(idsToGenerate, idList.size)
		assertEquals(idList.size, idSet.size)
		println("Throughput: ${idsToGenerate/(end - start).toDouble()} ids/ms")
	}

	private suspend fun genIds(count: Int): List<Id128>
	{
		val gen = IdGen128(100)
		println(gen.nodeId)
		return (0 until count).map { gen.generate(1000) }
				.toList()
	}
}