package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.pipeline.backend.GpuInstances
import de.fabmax.kool.scene.MeshInstanceList
import de.fabmax.kool.scene.geometry.Usage
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.checkIsNotReleased

class GpuInstancesGl(
    val instances: MeshInstanceList<*>,
    val backend: RenderBackendGl,
    creationInfo: BufferCreationInfo
) : BaseReleasable(), GpuInstances {

    internal val instanceBuffer: GpuBufferGl

    private val gl = backend.gl
    private var updateModCount = -1

    init {
        val namePrefix = creationInfo.bufferName
        instanceBuffer =
            GpuBufferGl(gl.ARRAY_BUFFER, backend, creationInfo.copy(bufferName = "$namePrefix.instances"))
    }

    fun checkBuffers() {
        checkIsNotReleased()
        if (instances.modCount.isDirty(updateModCount)) {
            updateModCount = instances.modCount.count
            instanceBuffer.setData(instances.instanceData.buffer, instances.usage.glUsage)
        }
    }

    private val Usage.glUsage: Int get() = when (this) {
        Usage.DYNAMIC -> gl.DYNAMIC_DRAW
        Usage.STATIC -> gl.STATIC_DRAW
    }

    override fun doRelease() {
        instanceBuffer.release()
    }
}