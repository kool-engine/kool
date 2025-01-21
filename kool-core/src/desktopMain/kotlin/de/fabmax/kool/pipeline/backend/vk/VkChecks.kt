package de.fabmax.kool.pipeline.backend.vk

import org.lwjgl.PointerBuffer
import org.lwjgl.system.MemoryStack
import org.lwjgl.vulkan.VK10
import java.nio.LongBuffer

@PublishedApi
internal fun checkVk(code: Int, msg: (String) -> String = { "Vulkan operation failed (error: $code)" }) {
    check(code == VK10.VK_SUCCESS) { msg("${VK_ERROR_CODES[code] ?: "unknown"} ($code)") }
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

private val VK_ERROR_CODES = mapOf(
    0 to "VK_SUCCESS",
    1 to "VK_NOT_READY",
    2 to "VK_TIMEOUT",
    3 to "VK_EVENT_SET",
    4 to "VK_EVENT_RESET",
    5 to "VK_INCOMPLETE",
    -1 to "VK_ERROR_OUT_OF_HOST_MEMORY",
    -2 to "VK_ERROR_OUT_OF_DEVICE_MEMORY",
    -3 to "VK_ERROR_INITIALIZATION_FAILED",
    -4 to "VK_ERROR_DEVICE_LOST",
    -5 to "VK_ERROR_MEMORY_MAP_FAILED",
    -6 to "VK_ERROR_LAYER_NOT_PRESENT",
    -7 to "VK_ERROR_EXTENSION_NOT_PRESENT",
    -8 to "VK_ERROR_FEATURE_NOT_PRESENT",
    -9 to "VK_ERROR_INCOMPATIBLE_DRIVER",
    -10 to "VK_ERROR_TOO_MANY_OBJECTS",
    -11 to "VK_ERROR_FORMAT_NOT_SUPPORTED",
    -12 to "VK_ERROR_FRAGMENTED_POOL",
    -13 to "VK_ERROR_UNKNOWN",
)