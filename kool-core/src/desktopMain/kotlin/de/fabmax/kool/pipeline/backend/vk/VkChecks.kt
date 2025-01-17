package de.fabmax.kool.pipeline.backend.vk

import org.lwjgl.PointerBuffer
import org.lwjgl.system.MemoryStack
import org.lwjgl.vulkan.VK10
import java.nio.LongBuffer

@PublishedApi
internal fun checkVk(code: Int, msg: (Int) -> String = { "Vulkan operation failed (error: $code)" }) {
    check(code == VK10.VK_SUCCESS) { msg(code) }
}

internal inline fun MemoryStack.checkCreateLongPtr(block: MemoryStack.(LongBuffer) -> Int): Long {
    val lp = mallocLong(1)
    checkVk(block(lp))
    return lp[0]
}

internal inline fun MemoryStack.checkCreatePointer(block: MemoryStack.(PointerBuffer) -> Int): Long {
    val lp = mallocPointer(1)
    checkVk(block(lp))
    return lp[0]
}