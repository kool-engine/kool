package de.fabmax.kool.scene

import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.GpuType
import de.fabmax.kool.pipeline.backend.GpuInstances
import de.fabmax.kool.scene.geometry.Usage
import de.fabmax.kool.util.*
import kotlin.math.max

@Deprecated("Use a layout struct instead of specifying instance attributes individually")
fun MeshInstanceList(instanceAttributes: List<Attribute>, initialSize: Int = 100): MeshInstanceList<*> {
    val layout = DynamicStruct("InstanceLayout", MemoryLayout.TightlyPacked) {
        instanceAttributes.forEach {
            when (it.type) {
                GpuType.Float1 -> float1(it.name)
                GpuType.Float2 -> float2(it.name)
                GpuType.Float3 -> float3(it.name)
                GpuType.Float4 -> float4(it.name)
                GpuType.Int1 -> int1(it.name)
                GpuType.Int2 -> int2(it.name)
                GpuType.Int3 -> int3(it.name)
                GpuType.Int4 -> int4(it.name)
                GpuType.Mat2 -> mat2(it.name)
                GpuType.Mat3 -> mat3(it.name)
                GpuType.Mat4 -> mat4(it.name)
                else -> error("${it.type} (${it.name}) not supported here")
            }
        }
    }
    return MeshInstanceList(layout, initialSize)
}

@Suppress("DEPRECATION")
@Deprecated("Use a layout struct instead of specifying instance attributes individually")
fun MeshInstanceList(initialSize: Int, vararg instanceAttributes: Attribute): MeshInstanceList<*> =
    MeshInstanceList(listOf(*instanceAttributes), initialSize)

class MeshInstanceList<T: Struct>(val layout: T, initialSize: Int = 100, val isResizable: Boolean = true) : BaseReleasable() {
    var instanceData = StructBuffer(layout, initialSize).apply { limit = 0 }
        private set

    /**
     * Expected usage of data in this instance list: STATIC if attributes are expected to change very infrequently /
     * never, DYNAMIC (the default value) if they will be updated often.
     */
    var usage = Usage.DYNAMIC

    /**
     * Number of instances.
     */
    var numInstances: Int
        get() = instanceData.limit
        set(value) {
            instanceData.limit = value
            incrementModCount()
        }
    val maxInstances: Int get() = instanceData.capacity

    val modCount = ModCounter()

    var gpuInstances: GpuInstances? = null

    fun incrementModCount() = modCount.increment()

    @PublishedApi
    internal fun checkBufferSize(reqSpace: Int) {
        if (reqSpace <= 0 || layout.structSize == 0) return
        if (numInstances + reqSpace > maxInstances) {
            check(isResizable) { "Maximum buffer size exceeded and instance buffer is not resizable" }
            val newSize = max(maxInstances * 2, numInstances + reqSpace)
                .coerceAtMost(Int.MAX_VALUE / layout.structSize)
            check(newSize >= numInstances + reqSpace) {
                "Unable to increase instance buffer size to required size of ${numInstances + reqSpace} instances. " +
                        "Maximum size is ${Int.MAX_VALUE / layout.structSize} instances / ${Int.MAX_VALUE} bytes."
            }
            val newData = StructBuffer(layout, newSize)
            newData.limit = instanceData.limit
            newData.putAll(instanceData)
            instanceData = newData
        }
    }

    inline fun addInstances(numInstances: Int, block: (StructBuffer<T>) -> Unit) {
        checkBufferSize(numInstances)
        block(instanceData)
        incrementModCount()
    }

    inline fun addInstance(block: MutableStructBufferView<T>.(T) -> Unit) {
        addInstances(1) { buf -> buf.put(block) }
    }

    fun clear() {
        if (numInstances == 0) {
            return
        }
        instanceData.clear()
        instanceData.limit = 0
        incrementModCount()
    }

    /**
     * Replaces all content by the content of [source]. The given source buffer must have the same instance layout.
     * This instance list's [modCount] is set to the [modCount] value of the source instance list.
     */
    internal fun set(source: MeshInstanceList<*>) {
        require(source.layout == layout) { "Source instance layout does not match this instance layout" }
        clear()
        checkBufferSize(source.instanceData.limit)
        @Suppress("UNCHECKED_CAST")
        instanceData.putAll(source.instanceData as StructBuffer<T>)
        usage = source.usage
        modCount.reset(source.modCount)
        numInstances = source.numInstances
    }

    override fun doRelease() {
        gpuInstances?.releaseDelayed(1)
    }
}

object InstanceLayoutEmpty : Struct("InstanceLayoutEmpty", MemoryLayout.TightlyPacked)

object InstanceLayoutModelMat : Struct("InstanceLayoutModelMat", MemoryLayout.TightlyPacked) {
    val modelMat = mat4(Attribute.INSTANCE_MODEL_MAT.name)
}

object InstanceLayoutModelMatAndColor : Struct("InstanceLayoutModelMatAndColor", MemoryLayout.TightlyPacked) {
    val modelMat = mat4(Attribute.INSTANCE_MODEL_MAT.name)
    val color = float4(Attribute.INSTANCE_COLOR.name)
}
