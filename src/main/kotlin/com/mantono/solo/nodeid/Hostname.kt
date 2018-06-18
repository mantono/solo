package com.mantono.solo.nodeid

import com.mantono.pyttipanna.HashAlgorithm
import com.mantono.pyttipanna.hash
import com.mantono.solo.api.NodeIdProvider
import java.io.File
import java.nio.file.Files
import kotlin.streams.toList

class Hostname(private val envVars: EnvironmentVariableReader = EnvironmentVariables): NodeIdProvider
{
	override fun nodeId(): ByteArray
	{
		val hostname: String = hostname(envVars)
		return hash(hostname, algorithm = HashAlgorithm.SHA256)
	}
}

fun hostname(env: EnvironmentVariableReader): String
{
	val envVarComputerName: String? = env.readEnv("COMPUTERNAME")
	val envVarHostName: String? = env.readEnv("HOSTNAME")
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