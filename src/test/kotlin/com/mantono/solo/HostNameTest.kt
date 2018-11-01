package com.mantono.solo

import com.mantono.solo.nodeid.EnvironmentVariableReader
import com.mantono.solo.nodeid.Hostname
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class HostNameTest {
    private class EnvironmentMock(
        private val envVars: Map<String, String>
    ) : EnvironmentVariableReader {
        override fun readEnv(variable: String, default: String): String =
            throw NotImplementedError()

        override fun readEnv(variable: String): String? = envVars[variable]
    }

    @Test
    fun testHostNameIsFoundForEnvComputerName() {
        val envMock = EnvironmentMock(mapOf("COMPUTERNAME" to "Ada"))
        val hostnameProvider = Hostname(envMock)
        assertTrue(hostnameProvider.nodeId().isNotEmpty())
    }

    @Test
    fun testHostNameIsFoundForEnvHostName() {
        val envMock = EnvironmentMock(mapOf("HOSTNAME" to "Lovelace"))
        val hostnameProvider = Hostname(envMock)
        assertTrue(hostnameProvider.nodeId().isNotEmpty())
    }

    @Test
    fun testHostNameIsDifferent() {
        val envMockLovelace = EnvironmentMock(mapOf("HOSTNAME" to "Lovelace"))
        val hostnameLovelace = Hostname(envMockLovelace)

        val envMockAda = EnvironmentMock(mapOf("COMPUTERNAME" to "Ada"))
        val hostnameAda = Hostname(envMockAda)
        assertNotEquals(hostnameLovelace.nodeId(), hostnameAda.nodeId())
    }
}