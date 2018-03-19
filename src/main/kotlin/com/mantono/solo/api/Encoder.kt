package com.mantono.solo.api

typealias Encoder<T> = (timestamp: Long, nodeId: ByteArray, sequence: Long) -> T