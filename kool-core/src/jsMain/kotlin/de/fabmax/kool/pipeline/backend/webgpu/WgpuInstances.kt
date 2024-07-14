package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.pipeline.backend.GpuInstances
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshInstanceList
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.checkIsNotReleased

class WgpuInstances(mesh: Mesh, val backend: RenderBackendWebGpu) : BaseReleasable(), GpuInstances {
    private val device: GPUDevice get() = backend.device

    private val instances: MeshInstanceList = checkNotNull(mesh.instances)
    private val createdInstanceBuffer: WgpuVertexBuffer = WgpuVertexBuffer(backend, "${mesh.name} instance data", instances.strideBytesF * instances.maxInstances)

    val instanceBuffer: GPUBuffer get() = createdInstanceBuffer.buffer.buffer

    private var isNewlyCreated = true

    fun checkBuffers() {
        checkIsNotReleased()

        if (instances.hasChanged || isNewlyCreated) {
            createdInstanceBuffer.writeData(instances.dataF)
            instances.hasChanged = false
        }
        isNewlyCreated = false
    }

    override fun release() {
        createdInstanceBuffer.buffer.release()
        super.release()
    }
}