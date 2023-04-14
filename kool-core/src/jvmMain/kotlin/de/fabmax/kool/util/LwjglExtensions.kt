package de.fabmax.kool.util

import org.lwjgl.system.MemoryStack
import java.nio.ByteBuffer
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

fun String.toByteBuffer(): ByteBuffer {
    val bytes = toByteArray()
    val buffer = ByteBuffer.allocateDirect(bytes.size + 1)
    buffer.put(bytes)
    buffer.put(0)
    buffer.flip()
    return buffer
}

inline fun <R> memStack(block: (MemoryStack.() -> R)): R {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return MemoryStack.stackPush().use { stack ->
        stack.block()
    }
}