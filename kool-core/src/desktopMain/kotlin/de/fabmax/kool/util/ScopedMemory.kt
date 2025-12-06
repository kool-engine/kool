package de.fabmax.kool.util

import org.lwjgl.system.MemoryStack
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

inline fun <R> scopedMem(parent: MemoryStack? = null, block: (MemoryStack.() -> R)): R {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    val stack = parent ?: MemoryStack.stackPush()
    try {
        return stack.block()
    } finally {
        if (parent == null) {
            stack.close()
        }
    }
}