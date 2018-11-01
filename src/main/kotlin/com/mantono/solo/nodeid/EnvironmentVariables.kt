package com.mantono.solo.nodeid

object EnvironmentVariables : EnvironmentVariableReader {
    override fun readEnv(variable: String, default: String): String =
        System.getenv(variable) ?: default

    override fun readEnv(variable: String): String? = System.getenv(variable)
}