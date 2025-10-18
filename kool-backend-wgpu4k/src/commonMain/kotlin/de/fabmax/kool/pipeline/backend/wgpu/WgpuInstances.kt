package de.fabmax.kool.pipeline.backend.wgpu

import de.fabmax.kool.pipeline.backend.GpuInstances
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshInstanceList
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.checkIsNotReleased
import io.ygdrasil.webgpu.GPUBuffer
import io.ygdrasil.webgpu.GPUDevice

class WgpuInstances(val instances: MeshInstanceList<*>, val backend: RenderBackendWgpu4k, mesh: Mesh<*>) : BaseReleasable(), GpuInstances {
    private val device: GPUDevice get() = backend.device

    private val createdInstanceBuffer: WgpuGrowingBuffer? = if (instances.layout.structSize == 0) null else {
        WgpuGrowingBuffer(
            backend = backend,
            label = "${mesh.name} instance data", instances.layout.structSize * instances.maxInstances.toLong(),
        )
    }
    val instanceBuffer: GPUBuffer? get() = createdInstanceBuffer?.buffer?.buffer

    private var updateModCount = -1

    fun checkBuffers() {
        checkIsNotReleased()

        if (instances.modCount.isDirty(updateModCount)) {
            updateModCount = instances.modCount.count
            createdInstanceBuffer?.writeData(instances.instanceData.buffer)
        }
    }

    override fun doRelease() {
        createdInstanceBuffer?.release()
    }
}