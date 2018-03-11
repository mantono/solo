package com.mantono.solo

import com.mantono.solo.api.Id128
import kotlinx.coroutines.experimental.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class IdGen128BitsTest
{
	@Test
	fun uniquenessTest()
	{
		val idList: List<Id128> = runBlocking { genIds(100) }
		val idSet: Set<Id128> = idList.toSet()

		assertEquals(idList.size, idSet.size)
	}

	private suspend fun genIds(count: Int): List<Id128>
	{
		val gen = IdGen128(count)
		val id = gen.generate(100)
		return listOf(id)
	}
}