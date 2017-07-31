package de.fabmax.kool

import de.fabmax.kool.gl.GlResource

/**
 * @author fabmax
 */
class MemoryManager internal constructor() {

    private val allocationMap: MutableMap<GlResource.Type, MutableMap<GlResource, Int>> = mutableMapOf()
    private val totalMem: MutableMap<GlResource.Type, Double> = mutableMapOf()

    val numTextures: Int get() = allocationMap[GlResource.Type.TEXTURE]!!.size
    val numShaders: Int get() = allocationMap[GlResource.Type.PROGRAM]!!.size
    val numBuffers: Int get() = allocationMap[GlResource.Type.BUFFER]!!.size

    init {
        for (type in GlResource.Type.values()) {
            allocationMap.put(type, mutableMapOf())
            totalMem.put(type, 0.0)
        }
    }

    fun getTotalMemory(type: GlResource.Type): Double {
        // total memory is stored as double because Long is slow in javascript
        return totalMem[type]!!
    }

    fun memoryAllocated(resource: GlResource, memory: Int) {
        val prevAlloc = allocationMap[resource.type]!!.put(resource, memory) ?: 0

        if (prevAlloc != memory) {
            val newTotal = totalMem[resource.type]!! + memory - prevAlloc
            totalMem.put(resource.type, newTotal)

            //println("${resource.type} allocated: ${memory-prevAlloc} (total: $newTotal)")
        }
    }

    fun deleted(resource: GlResource) {
        val memory = allocationMap[resource.type]?.remove(resource)

        if (memory != null) {
            val newTotal = totalMem[resource.type]!! - memory
            totalMem.put(resource.type, newTotal)

            //println("${resource.type} deleted: $memory  (total: $newTotal)")
        }
    }

}
