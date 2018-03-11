package com.mantono.solo

import java.io.IOException
import java.net.NetworkInterface

fun getMacAddress(): ByteArray
{
	if(nonVirtualNetWorkInterfaces().count() == 0)
		throw IOException("Found no interface to retrieve MAC address from")
	return firstInterface().hardwareAddress
}

fun nonVirtualNetWorkInterfaces(): Sequence<NetworkInterface> = NetworkInterface
		.getNetworkInterfaces()
		.asSequence()
		.filterNot { it.isLoopback }
		.filterNot { it.isVirtual }

private fun firstInterface(): NetworkInterface = nonVirtualNetWorkInterfaces().first()