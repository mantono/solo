package com.mantono.solo.encoders

import com.mantono.solo.api.Encoder
import com.mantono.solo.api.Identifier
import java.math.BigInteger

abstract class BitEncoder<out T : Identifier>(
    override val timestampBits: Int,
    override val nodeIdBits: Int,
    override val sequenceBits: Int
) : Encoder<T> {
    val totalBits: Int by lazy { nodeIdBits + timestampBits + sequenceBits }
    val totalBytes: Int = totalBits / 8

    init {
        if (totalBits % 8 != 0)
            throw IllegalArgumentException("The total amount of bits is not a multiple of 8")
    }

    fun assertLength(bits: Int) {
        if (bits != totalBits)
            throw IllegalStateException("Bad bit count: $totalBits != $bits")
    }

    fun generateByteArray(timestamp: Long, nodeId: ByteArray, sequence: Long): ByteArray {
        val ts: BigInteger = timestamp
            .toBigInteger()
            .reduceToLeastSignificantBits(timestampBits - 1)
            .shl(nodeIdBits + sequenceBits)

        val node: BigInteger = BigInteger(nodeId)
            .reduceToLeastSignificantBits(nodeIdBits)
            .shl(sequenceBits)

        val seq: BigInteger = sequence.toBigInteger()

        val outcome: BigInteger = (ts xor node xor seq).abs()
        return outcome.toByteArray().expandTo(totalBytes)
    }
}

fun BigInteger.reduceToLeastSignificantBits(bits: Int): BigInteger {
    if (bits == 0)
        return this

    val mask = this shr bits shl bits
    return (this xor mask).also {
        require(it.bitCount() <= bits) { "${it.bitCount()} > $bits" }
    }
}

fun Long.toBigInteger(): BigInteger = BigInteger.valueOf(this)

private fun ByteArray.expandTo(newSize: Int): ByteArray {
    val bytesDiff: Int = newSize - size
    return when {
        bytesDiff > 0 -> ByteArray(bytesDiff) { 0 } + this
        bytesDiff == 0 -> this
        else -> throw IllegalStateException("Byte delta is negative")
    }.also {
        require(it.size == newSize) { "Expected $newSize bytes, got ${it.size}" }
    }
}