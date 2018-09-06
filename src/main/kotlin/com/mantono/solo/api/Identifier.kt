package com.mantono.solo.api

import toBase64

@ExperimentalUnsignedTypes
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
	fun entropy(): UInt = asBytes().size.toUInt() * 8u

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

private val base64Regex = Regex("^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{4})\$")

@ExperimentalUnsignedTypes
		/**
 * Allows for an [Identifier] that has been serialized in the form of an encoded base64 String to
 * be decoded and converted back to an [Identifier].
 */
fun identifierFrom(base64Input: String): Identifier
{
	if(!base64Input.matches(base64Regex))
		throw IllegalArgumentException("Input $base64Input is not a valid base64 encoded data")

	val bytes: ByteArray = java.util.Base64.getDecoder().decode(base64Input)
	return object: Identifier
	{
		override fun asBytes(): ByteArray = bytes
	}
}