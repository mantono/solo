package com.mantono.solo

import com.mantono.solo.api.Identifier
import com.mantono.solo.generator.IdGen
import kotlinx.coroutines.experimental.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Instant

class IdentifierGen128BitsTest
{
	@Test
	fun uniquenessTest()
	{
		val idsToGenerate = 400_000
		val start: Long = Instant.now().toEpochMilli()
		val idList: List<Identifier> = runBlocking { genIds(idsToGenerate) }
		val end: Long = Instant.now().toEpochMilli()
		val idSet: Set<Identifier> = idList.toSet()

		assertEquals(idsToGenerate, idList.size)
		assertEquals(idList.size, idSet.size)
		println("Throughput: ${idsToGenerate/(end - start).toDouble()} ids/ms")
	}

	private suspend fun genIds(count: Int): List<Identifier>
	{
		val gen = IdGen<Identifier>(100, Default64BitEncoder)
		println(gen.nodeId)
		return (0 until count).map { gen.generate(1000) }
				.toList()
	}
}