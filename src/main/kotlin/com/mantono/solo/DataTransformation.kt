package com.mantono.solo

import java.math.BigInteger
import java.util.*

fun BigInteger.toBits(): List<Byte>
{
	val bits = BitSet.valueOf(this.toByteArray())
	System.out.println(bits.cardinality())
	return emptyList()
}

fun Long.toBitString(): String = BigInteger.valueOf(this).toBitsString()
fun String.toBitString(): String = BigInteger(this).toBitsString()

fun BigInteger.toBitsString(): String
{
	val negative: Boolean = signum() <= -1
	val x = if(negative) inv().plus(BigInteger.ONE) else this
	return x.toString(2)
			.let {
				val leftPad: Int = 8 - (it.length % 8)
				val padded = String(CharArray(leftPad) { '0' }) + it
				if(negative) invert(padded) else padded
			}
			.mapIndexed { index, c ->
				if(index % 8 == 0 && index != 0) " $c" else "$c"
			}
			.joinToString(separator = "") { it }
}

private fun invert(bits: String): String = bits.asSequence()
		.map {
			when(it) {
				'0' -> '1'
				'1' -> '0'
				else -> it
			}
		}
		.joinToString(separator = "") { "$it" }