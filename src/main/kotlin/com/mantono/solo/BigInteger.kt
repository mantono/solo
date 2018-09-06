package com.mantono.solo

import java.math.BigInteger

fun BigInteger.reduceToLeastSignificantBits(bits: Int): BigInteger
{
	if(bits == 0)
		return this

	val mask = this shr bits shl bits
	return (this xor mask).also {
		require(it.bitCount() <= bits) { "${it.bitCount()} > $bits" }
	}

}

@ExperimentalUnsignedTypes
fun BigInteger.reduceToLeastSignificantBits(bits: UInt): BigInteger
{
	if(bits == 0u)
		return this

	val signedBits: Int = bits.toInt()

	val mask = this shr signedBits shl signedBits
	return (this xor mask).also {
		require(it.bitCount() <= signedBits) { "${it.bitCount()} > $signedBits" }
	}

}

fun Long.toBigInteger(): BigInteger = BigInteger.valueOf(this)
@ExperimentalUnsignedTypes
fun ULong.toBigInteger(): BigInteger = BigInteger.valueOf(this.toLong())

@ExperimentalUnsignedTypes
infix fun BigInteger.shl(n: UInt): BigInteger = this.shl(n.toInt())
@ExperimentalUnsignedTypes
infix fun BigInteger.shr(n: UInt): BigInteger = this.shr(n.toInt())