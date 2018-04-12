package com.mantono.solo

import java.math.BigInteger
import java.util.*

fun BigInteger.toBits(): List<Byte>
{
	val bits = BitSet.valueOf(this.toByteArray())
	System.out.println(bits.cardinality())
	return emptyList()
}

fun BigInteger.toBitsString(): String
{
	val x = if(signum() <= -1) inv() else this
	return x.toString(2)
			.let {
				val padWith: Char = if(signum() >= 0) '0' else '1'
				val leftPad: Int = 8 - (it.length % 8)
				String(CharArray(leftPad) { padWith }) + it
			}
			.mapIndexed { index, c ->
				if(index % 8 == 0 && index != 0) " $c" else "$c"
			}
			.joinToString(separator = "") { it }
}