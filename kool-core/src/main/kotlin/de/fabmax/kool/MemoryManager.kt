package de.fabmax.kool

/**
 * @author fabmax
 */
class MemoryManager internal constructor() {

    private val allocationMap: MutableMap<GlResource.Type, MutableMap<GlResource, Int>> = mutableMapOf()

    internal fun memoryAllocated(resource: GlResource, memory: Int) {
        var resMap = allocationMap[resource.type]
        if (resMap == null) {
            resMap = mutableMapOf()
            allocationMap[resource.type] = resMap
        }
        resMap.put(resource, memory)

        println("${resource.type} allocated: $memory bytes")
    }

    internal fun deleted(resource: GlResource) {
        val memory = allocationMap[resource.type]?.remove(resource)

        if (memory != null) {
            println("${resource.type} deleted: $memory bytes")
        }
    }

}
