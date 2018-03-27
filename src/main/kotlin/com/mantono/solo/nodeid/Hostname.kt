package com.mantono.solo.nodeid

import com.mantono.pyttipanna.Algorithm
import com.mantono.pyttipanna.hash
import com.mantono.solo.api.NodeIdProvider
import java.io.File
import java.nio.file.Files
import kotlin.streams.toList

object Hostname: NodeIdProvider
{
	override fun nodeId(): ByteArray
	{
		val hostname: String = hostname()
		return hash(hostname, algorithm = Algorithm.SHA256)
	}
}

fun hostname(): String
{
	val envVarComputerName: String? = System.getenv("COMPUTERNAME")
	val envVarHostName: String? = System.getenv("HOSTNAME")
	val hostnameFile: String? = readHostnameFile()

	return sequenceOf(envVarComputerName, envVarHostName, hostnameFile)
			.filterNot { it.isNullOrBlank() }
			.first()!!
}

private fun readHostnameFile(): String?
{
	val hostnameFile = File("/etc/hostname")
	if(!hostnameFile.exists())
		return null
	val lines: List<String> = Files.lines(hostnameFile.toPath()).toList()
	if(lines.isEmpty())
		return null

	return lines.first()
}