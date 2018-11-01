package com.mantono.solo.nodeid

interface EnvironmentVariableReader {
    fun readEnv(variable: String, default: String): String
    fun readEnv(variable: String): String?
}