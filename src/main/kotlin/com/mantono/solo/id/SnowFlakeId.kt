package com.mantono.solo.id

import com.mantono.pyttipanna.transformation.Base64
import com.mantono.solo.api.Identifier
import java.nio.ByteBuffer
import java.util.Arrays

class SnowFlakeId(private val bytes: ByteArray) : Identifier {
    override fun asBytes(): ByteArray = bytes.copyOf()
    override fun asString(): String = Base64.asString(bytes)
    fun asLong(): Long = bytes.toLong()

    override fun equals(other: Any?): Boolean
    {
        if (this === other) return true
        if (other !is Identifier) return false

        return this.bytes.contentEquals(other.asBytes())
    }

    override fun hashCode(): Int = Arrays.hashCode(bytes)
}

fun ByteArray.toLong(): Long
{
    val buffer = ByteBuffer.allocate(8)
    buffer.put(this)
    buffer.flip()
    return buffer.getLong(0)
}