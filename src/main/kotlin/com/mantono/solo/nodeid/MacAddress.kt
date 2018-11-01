package com.mantono.solo.nodeid

import com.mantono.pyttipanna.hashing.HashAlgorithm
import com.mantono.pyttipanna.hashing.hash
import com.mantono.solo.api.NodeIdProvider
import java.io.IOException
import java.net.NetworkInterface

object MacAddress: NodeIdProvider
{
	override fun nodeId(): ByteArray
	{
		if(nonVirtualNetWorkInterfaces().count() == 0)
			throw IOException("Found no interface to retrieve MAC address from")
		val hardwareAddress: ByteArray = firstInterface().hardwareAddress
		return hash(hardwareAddress, algorithm = HashAlgorithm.SHA256)
	}
}

fun nonVirtualNetWorkInterfaces(): Sequence<NetworkInterface> = NetworkInterface
		.getNetworkInterfaces()
		.asSequence()
		.filterNot { it.isLoopback }
		.filterNot { it.isVirtual }

private fun firstInterface(): NetworkInterface = nonVirtualNetWorkInterfaces().first()