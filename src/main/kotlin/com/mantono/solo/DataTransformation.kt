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
				val leftPad: Int = 8 - it.length
				String(CharArray(leftPad) { padWith }) + it
			}
}

/**
fun BigInteger.toBits(): List<Byte>
{
	System.out.println("$this -> ${this.bitLength()} / ${this.bitCount()}")
	val bitRange: IntRange = 0 .. bitLength()

	return bitRange.asSequence()
			.map {
				when(testBit(it))
				{
					true -> 1.toByte()
					false -> 0.toByte()
				}
			}
			.toList()
}

fun BigInteger.toBitsString(): String = toBits()
		.mapIndexed { index, s -> if(index % 8 == 0 && index > 0) "$s " else s.toString() }
		.joinToString(separator = "") { it }
		.let {
			val padWith: Char = if(signum() >= 0) '0' else '1'
			val leftPad: Int = 8 - it.length
			String(CharArray(leftPad) { padWith }) + it
		}
		**/