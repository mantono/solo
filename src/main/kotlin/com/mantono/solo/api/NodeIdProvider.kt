package com.mantono.solo.api

/**
 * A NodeIdProvider offers means to uniquely identify a node. A "_node_"
 * could be a physical machine, a virtual machine or a process. This can
 * be identified by means such as a MAC address, an IP address or a process id.
 * What kind of identification that is suitable depends on the context and
 * the environment of the application using the generated [Identifier].
 */
interface NodeIdProvider {
    /**
	 * An ID that uniquely identifies this generator. This can but must not
	 * necessarily be unique for a physical node. The important thing is
	 * that each generator has an unique id, regardless of from what it is
	 * derived. This ID should remain constant and not change during runtime.
	 *
	 * @return a ByteArray that uniquely identifies this node
	 */
    fun nodeId(): ByteArray
}