package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.pipeline.backend.GpuGeometry
import de.fabmax.kool.pipeline.isInt
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.scene.geometry.Usage
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.checkIsNotReleased

class GpuGeometryGl(
    val geometry: IndexedVertexList,
    val backend: RenderBackendGl,
    creationInfo: BufferCreationInfo
) : BaseReleasable(), GpuGeometry {

    internal val indexBuffer: GpuBufferGl
    internal val dataBufferF: GpuBufferGl?
    internal val dataBufferI: GpuBufferGl?

    private val gl = backend.gl

    private var isNewlyCreated =  true
    var numIndices = 0

    private val name = creationInfo.bufferName

    init {
        val namePrefix = creationInfo.bufferName
        indexBuffer = GpuBufferGl(gl.ELEMENT_ARRAY_BUFFER, backend, creationInfo.copy(bufferName = "$namePrefix.${geometry.name}.indices"))

        val hasFloatAttributes = geometry.vertexAttributes.any { !it.type.isInt }
        dataBufferF = if (hasFloatAttributes) {
            GpuBufferGl(gl.ARRAY_BUFFER, backend, creationInfo.copy(bufferName = "$namePrefix.${geometry.name}.dataF"))
        } else {
            null
        }

        val hasIntAttributes = geometry.vertexAttributes.any { it.type.isInt }
        dataBufferI = if (hasIntAttributes) {
            GpuBufferGl(gl.ARRAY_BUFFER, backend, creationInfo.copy(bufferName = "$namePrefix.${geometry.name}.dataI"))
        } else {
            null
        }
    }

    fun checkBuffers() {
        checkIsNotReleased()
        if (!geometry.isBatchUpdate && (geometry.hasChanged || isNewlyCreated)) {
            numIndices = geometry.numIndices

            val usage = geometry.usage.glUsage
            indexBuffer.setData(geometry.indices, usage)
            dataBufferF?.setData(geometry.dataF, usage)
            dataBufferI?.setData(geometry.dataI, usage)

            geometry.hasChanged = false
        }
        isNewlyCreated = false
    }

    override fun toString(): String {
        return "GpuGeometryGl(name=$name, geometry.name=${geometry.name})"
    }

    override fun release() {
        indexBuffer.release()
        dataBufferF?.release()
        dataBufferI?.release()
        super.release()
    }

    private val Usage.glUsage: Int get() = when (this) {
        Usage.DYNAMIC -> gl.DYNAMIC_DRAW
        Usage.STATIC -> gl.STATIC_DRAW
    }
}