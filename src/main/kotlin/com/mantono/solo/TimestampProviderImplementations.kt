package com.mantono.solo

import com.mantono.solo.api.TimestampProvider
import java.time.Instant

object MillisecondsSinceUnixEpoch: TimestampProvider
{
	override fun timestamp(): Long = Instant.now().toEpochMilli()
}

object SecondsSinceUnixEpoch: TimestampProvider
{
	override fun timestamp(): Long = Instant.now().epochSecond
}