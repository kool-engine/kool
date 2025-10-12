package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.pipeline.backend.GpuGeometry
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.scene.geometry.Usage
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.checkIsNotReleased

class GpuGeometryGl(
    val geometry: IndexedVertexList<*>,
    val backend: RenderBackendGl,
    creationInfo: BufferCreationInfo
) : BaseReleasable(), GpuGeometry {

    internal val indexBuffer: GpuBufferGl
    internal val dataBuffer: GpuBufferGl?

    private val gl = backend.gl

    private var updateModCount = -1
    var numIndices = 0

    private val name = creationInfo.bufferName

    init {
        val namePrefix = creationInfo.bufferName
        indexBuffer = GpuBufferGl(gl.ELEMENT_ARRAY_BUFFER, backend, creationInfo.copy(bufferName = "$namePrefix.${geometry.name}.indices"))

        val hasAttributes = geometry.layout.members.isNotEmpty()
        dataBuffer = if (hasAttributes) {
            GpuBufferGl(gl.ARRAY_BUFFER, backend, creationInfo.copy(bufferName = "$namePrefix.${geometry.name}.dataF"))
        } else {
            null
        }
    }

    fun checkBuffers() {
        checkIsNotReleased()
        if (geometry.modCount.isDirty(updateModCount)) {
            updateModCount = geometry.modCount.count
            numIndices = geometry.numIndices

            val usage = geometry.usage.glUsage
            indexBuffer.setData(geometry.indices, usage)
            dataBuffer?.setData(geometry.vertexData.buffer, usage)
        }
    }

    override fun toString(): String {
        return "GpuGeometryGl(name=$name, geometry.name=${geometry.name})"
    }

    override fun doRelease() {
        indexBuffer.release()
        dataBuffer?.release()
    }

    private val Usage.glUsage: Int get() = when (this) {
        Usage.DYNAMIC -> gl.DYNAMIC_DRAW
        Usage.STATIC -> gl.STATIC_DRAW
    }
}