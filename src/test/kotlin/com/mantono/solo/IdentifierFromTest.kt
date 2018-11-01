package com.mantono.solo

import com.mantono.pyttipanna.transformation.Base64
import com.mantono.solo.api.Identifier
import com.mantono.solo.api.identifierFrom
import com.mantono.solo.encoders.FlakeIdEncoder
import com.mantono.solo.generator.IdGen
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.concurrent.TimeUnit

class IdentifierFromTest
{
	@Test
	fun convertIdentifierToBase64AndBack()
	{
		val gen = IdGen<Identifier>(10, FlakeIdEncoder, nodeId = FakeMacAddress)
		runBlocking{
			val idBefore: Identifier = gen.generate(1, TimeUnit.SECONDS)
			val base64: String = Base64.asString(idBefore.asBytes())
			val idAfter: Identifier = identifierFrom(base64)
			assertEquals(idBefore, idAfter)
		}
	}

	@Test
	fun invalidBase64InputContainsSpacesForIdentifierFrom()
	{
		assertThrows<IllegalArgumentException>
		{
			identifierFrom("Not base64 data")
		}
	}

	@Test
	fun invalidBase64InputContainsTooManyEqualSignsForIdentifierFrom()
	{
		assertThrows<IllegalArgumentException>
		{
			identifierFrom("AAAAAFr3fNn04Gf3OQEAAA===")
		}
	}

	@Test
	fun invalidBase64InputMissingOneEqualSignForIdentifierFrom()
	{
		assertThrows<IllegalArgumentException>
		{
			identifierFrom("AAAAAFr3fNn04Gf3OQEAAA=")
		}
	}
}