package com.mantono.solo

import com.mantono.solo.api.NodeIdProvider
import java.io.IOException
import java.net.NetworkInterface

object MacAddress: NodeIdProvider
{
	override fun nodeId(): ByteArray
	{
		if(nonVirtualNetWorkInterfaces().count() == 0)
			throw IOException("Found no interface to retrieve MAC address from")
		return firstInterface().hardwareAddress
	}

}

fun nonVirtualNetWorkInterfaces(): Sequence<NetworkInterface> = NetworkInterface
		.getNetworkInterfaces()
		.asSequence()
		.filterNot { it.isLoopback }
		.filterNot { it.isVirtual }

private fun firstInterface(): NetworkInterface = nonVirtualNetWorkInterfaces().first()