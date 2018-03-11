package com.mantono.solo.bits

interface BitList: List<Boolean>
{
	fun toByteArray()
}

interface ImmutableBitList: BitList
{
	infix fun shl(n: Int): Bits
	infix fun shr(n: Int): Bits
	infix fun and(b: Bits): Bits
	infix fun or(b: Bits): Bits
	infix fun xor(b: Bits): Bits
	fun append(b: Bits): Bits
	fun reduceToMostSignificantSetBit(): Bits
	fun padLeftWithZeroes(leftPads: Int): Bits
	fun truncate(size: Int): Bits
}