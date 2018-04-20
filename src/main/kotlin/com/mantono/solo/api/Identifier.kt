package com.mantono.solo.api

import toBase64

interface Identifier
{

	/**
	 * By default this returns the
	 * number of bytes in the ByteArray (@see [asBytes]) times 8. This function should
	 * be overridden if an implementation
	 * chooses not to utilize all bits in each byte of the ByteArray
	 *
	 * @return the number of bits that this Identifier has.
	 */
	fun entropy(): Int = asBytes().size * 8

	/**
	 * @return a [ByteArray] which is the unique data representing this Identifier.
	 */
	fun asBytes(): ByteArray

	/**
	 * Gives a [String] representation of this Identifier in such way that it is unique for all
	 * instances with a given ByteArray. This representation is by default encoded as [java.util.Base64],
	 * but it is up to each implementation to decide what fits best.
	 * Examples of such encodings could be (but not limited to) base32, bas64 or
	 * a hexadecimal version. Most hash functions should be avoided since they have a risk of
	 * collision.
	 *
	 * @return a [String] representation of this Identifier
	 */
	fun asString(): String = asBytes().toBase64()
}