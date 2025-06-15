package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.pipeline.backend.GpuInstances
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshInstanceList
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.checkIsNotReleased

class WgpuInstances(val instances: MeshInstanceList, val backend: RenderBackendWebGpu, mesh: Mesh) : BaseReleasable(), GpuInstances {
    private val device: GPUDevice get() = backend.device

    private val createdInstanceBuffer: WgpuGrowingBuffer? = if (instances.instanceSizeF == 0) null else {
        WgpuGrowingBuffer(
            backend = backend,
            label = "${mesh.name} instance data", instances.strideBytesF * instances.maxInstances.toLong(),
        )
    }
    val instanceBuffer: GPUBuffer? get() = createdInstanceBuffer?.buffer?.buffer

    private var isNewlyCreated = true

    fun checkBuffers() {
        checkIsNotReleased()

        if (instances.hasChanged || isNewlyCreated) {
            createdInstanceBuffer?.writeData(instances.dataF)
            instances.hasChanged = false
        }
        isNewlyCreated = false
    }

    override fun release() {
        createdInstanceBuffer?.release()
        super.release()
    }
}