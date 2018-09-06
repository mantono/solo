package com.mantono.solo.api

@ExperimentalUnsignedTypes
interface TimestampProvider
{
	fun timestamp(): ULong
}