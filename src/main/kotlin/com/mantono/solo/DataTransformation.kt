package com.mantono.solo

import com.mantono.solo.api.Identifier
import java.math.BigInteger

fun BigInteger.toBits(): List<Byte> = toBitsString("", 0)
        .map { it.toString().toByte() }

fun Long.toBitString(delimiter: String = "", delimiterInterval: Int = 0): String =
    BigInteger.valueOf(this).toBitsString(delimiter, delimiterInterval)

fun String.toBitString(delimiter: String = "", delimiterInterval: Int = 0): String =
    BigInteger(this).toBitsString(delimiter, delimiterInterval)

fun ByteArray.toBitString(delimiter: String = "", delimiterInterval: Int = 0): String =
    BigInteger(this).toBitsString(delimiter, delimiterInterval)

fun Identifier.toBitString(delimiter: String = "", delimiterInterval: Int = 0): String =
    BigInteger(this.asBytes()).toBitsString(delimiter, delimiterInterval)

fun BigInteger.toBitsString(delimiter: String = " ", delimiterInterval: Int = 8): String {
    val negative: Boolean = signum() <= -1
    val unsigned = if (negative) inv().plus(BigInteger.ONE) else this
    return unsigned.toString(2)
            .padZeroes()
            .let { if (negative) invert(it) else it }
            .withDelimiter(delimiter, delimiterInterval)
}

private fun String.padZeroes(): String {
    val leftPad: Int = 8 - (length % 8)
    return String(CharArray(leftPad) { '0' }) + this
}

private fun String.withDelimiter(delimiter: String, delimiterInterval: Int): String {
    if (delimiterInterval == 0 || delimiter.isEmpty())
        return this

    return mapIndexed { index, c ->
        when {
            index == 0 -> "$c"
            index % delimiterInterval == 0 -> "$delimiter$c"
            else -> "$c"
        }
    }
            .joinToString(separator = "") { it }
}

private fun invert(bits: String): String = bits.asSequence()
    .map {
        when (it) {
            '0' -> '1'
            '1' -> '0'
            else -> it
        }
    }
    .joinToString(separator = "") { "$it" }