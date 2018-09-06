package com.mantono.solo

import com.mantono.solo.api.TimestampProvider
import java.time.Instant

@ExperimentalUnsignedTypes
object MillisecondsSinceUnixEpoch: TimestampProvider
{
	override fun timestamp(): ULong = Instant.now().toEpochMilli().toULong()
}

@ExperimentalUnsignedTypes
object SecondsSinceUnixEpoch: TimestampProvider
{
	override fun timestamp(): ULong = Instant.now().epochSecond.toULong()
}