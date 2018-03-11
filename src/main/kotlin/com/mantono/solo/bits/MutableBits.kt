package com.mantono.solo.bits

import java.util.*

data class MutableBits(private val bits: Deque<Boolean> = LinkedList()): Iterable<Boolean> by bits.asIterable()
{
	constructor(bits: Int): this(LinkedList(BooleanArray(bits) { false }.toList()))
	constructor(bits: BooleanArray): this(LinkedList(bits.toList()))

	override fun equals(other: Any?): Boolean
	{
		if(other !is MutableBits) return false
		return this.bits == other.bits
	}

	override fun hashCode(): Int = bits.hashCode()

	infix fun shl(n: Int): Bits
	{
		TODO()
	}
}