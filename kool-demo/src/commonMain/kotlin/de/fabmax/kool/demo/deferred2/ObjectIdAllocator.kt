package de.fabmax.kool.demo.deferred2

import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.NodeId
import de.fabmax.kool.util.logD
import de.fabmax.kool.util.logW
import kotlin.math.max

interface ObjectIdAllocator {
    val size: Int
    fun getIdRange(mesh: Mesh<*>): ObjectIdRange
    fun removeObject(mesh: Mesh<*>)
}

class DefaultObjectIdAllocator(val maxObjects: Int) : ObjectIdAllocator {
    override var size: Int = 1
        private set

    private val objectIdRanges = mutableMapOf<NodeId, ObjectIdRange>()
    private val slots = Array<NodeId?>(maxObjects) { null }

    override fun getIdRange(mesh: Mesh<*>): ObjectIdRange {
        return objectIdRanges.getOrPut(mesh.id) {
            val numInstances = mesh.instances?.maxInstances ?: 1
            var range = ObjectIdRange(size, size + numInstances)
            if (!checkRangeFree(range)) {
                var rng = searchFreeRange(numInstances)
                if (rng == null) {
                    logW { "Failed to find free object ID range for ${mesh.name} (size: $numInstances)" }
                    rng = ObjectIdRange(0, 1)
                }
                range = rng
            }
            logD { "Allocated object ID range for mesh ${mesh.name}: $range" }
            size = max(size, range.to)
            range
        }
    }

    override fun removeObject(mesh: Mesh<*>) {
        val range = objectIdRanges.remove(mesh.id)
        if (range != null) {
            slots.fill(null, range.from, range.to)
        }
    }

    private fun checkRangeFree(range: ObjectIdRange): Boolean {
        if (range.from >= maxObjects) {
            return false
        }
        for (i in range.from until range.to) {
            if (slots[i] != null) {
                return false
            }
        }
        return true
    }

    private fun searchFreeRange(size: Int): ObjectIdRange? {
        var from = 0
        while (from < maxObjects - size) {
            while (slots[from] != null) {
                from++
            }
            var range = 1
            while (range < size && slots[from + range] == null) {
                range++
            }
            if (range == size) {
                return ObjectIdRange(from, from + range)
            }
            from += range
        }
        return null
    }
}

data class ObjectIdRange(val from: Int, val to: Int) {
    val size: Int get() = to - from
}
