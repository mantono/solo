package com.mantono.solo

import com.mantono.solo.api.NodeIdProvider
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
	)
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
	)
}