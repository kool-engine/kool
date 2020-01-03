package de.fabmax.kool.platform.vk

import org.lwjgl.system.MemoryStack
import org.lwjgl.vulkan.VK10
import java.nio.LongBuffer

abstract class VkResource {

    var isDestroyed  = false
        private set
    private val dependingResources = mutableListOf<VkResource>()

    protected fun destroyDependingResources() {
        for (i in dependingResources.indices.reversed()) {
            dependingResources[i].destroy()
        }
    }

    protected abstract fun freeResources()

    fun addDependingResource(resource: VkResource) {
        dependingResources += resource
    }

    fun removeDependingResource(resource: VkResource) {
        dependingResources -= resource
    }

    fun destroy() {
        if (!isDestroyed) {
            destroyDependingResources()
            freeResources()
            isDestroyed = true
        }
    }

    fun checkVk(code: Int, msg: (Int) -> String = { "Check failed" }) {
        check(code == VK10.VK_SUCCESS) { msg(code) }
    }

    inline fun MemoryStack.checkCreatePointer(block: MemoryStack.(LongBuffer) -> Int): Long {
        val lp = mallocLong(1)
        checkVk(block(lp))
        return lp[0]
    }
}