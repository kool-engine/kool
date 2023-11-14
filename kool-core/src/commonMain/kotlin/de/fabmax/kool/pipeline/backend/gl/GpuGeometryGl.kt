package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.GlslType
import de.fabmax.kool.pipeline.VertexLayout
import de.fabmax.kool.pipeline.backend.GpuGeometry
import de.fabmax.kool.scene.MeshInstanceList
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.scene.geometry.Usage
import de.fabmax.kool.util.BaseReleasable

class GpuGeometryGl(
    val geometry: IndexedVertexList,
    val instances: MeshInstanceList?,
    val backend: RenderBackendGl,
    creationInfo: BufferCreationInfo
) : BaseReleasable(), GpuGeometry {

    internal val indexBuffer: BufferResource
    private val dataBufferF: BufferResource?
    private val dataBufferI: BufferResource?
    private val instanceBuffer: BufferResource?

    private val gl = backend.gl

    private var isNewlyCreated =  true
    var numIndices = 0

    init {
        val namePrefix = creationInfo.bufferName
        indexBuffer = BufferResource(gl.ELEMENT_ARRAY_BUFFER, backend, creationInfo.copy(bufferName = "$namePrefix.${geometry.name}.indices"))

        val hasFloatAttributes = geometry.vertexAttributes.any { !it.type.isInt }
        dataBufferF = if (hasFloatAttributes) {
            BufferResource(gl.ARRAY_BUFFER, backend, creationInfo.copy(bufferName = "$namePrefix.${geometry.name}.dataF"))
        } else {
            null
        }

        val hasIntAttributes = geometry.vertexAttributes.any { it.type.isInt }
        dataBufferI = if (hasIntAttributes) {
            BufferResource(gl.ARRAY_BUFFER, backend, creationInfo.copy(bufferName = "$namePrefix.${geometry.name}.dataI"))
        } else {
            null
        }

        instanceBuffer = if (instances != null) {
            BufferResource(gl.ARRAY_BUFFER, backend, creationInfo.copy(bufferName = "$namePrefix.${geometry.name}.instances"))
        } else {
            null
        }
    }

    override fun release() {
        indexBuffer.delete()
        dataBufferF?.delete()
        dataBufferI?.delete()
        instanceBuffer?.delete()
        super.release()
    }

    fun createShaderVertexAttributeBinders(shaderAttributes: Map<String, VertexLayout.VertexAttribute>): List<AttributeBinder> {
        val binders = mutableListOf<AttributeBinder>()
        geometry.vertexAttributes
            .filter { !it.type.isInt }
            .forEach { geomAttr ->
                val stride = geometry.byteStrideF
                val offset = geometry.attributeByteOffsets[geomAttr]!! / 4
                binders += makeAttribBinders(shaderAttributes, geomAttr, dataBufferF!!, stride, offset, gl.FLOAT)
            }
        geometry.vertexAttributes
            .filter { it.type.isInt }
            .forEach { geomAttr ->
                val stride = geometry.byteStrideI
                val offset = geometry.attributeByteOffsets[geomAttr]!! / 4
                binders += makeAttribBinders(shaderAttributes, geomAttr, dataBufferI!!, stride, offset, gl.INT)
            }
        return binders
    }

    fun createShaderInstanceAttributeBinders(shaderAttributes: Map<String, VertexLayout.VertexAttribute>): List<AttributeBinder> {
        instances ?: return emptyList()

        val binders = mutableListOf<AttributeBinder>()
        for (instanceAttrib in instances.instanceAttributes) {
            val stride = instances.strideBytesF
            val offset = instances.attributeOffsets[instanceAttrib]!! / 4
            binders += makeAttribBinders(shaderAttributes, instanceAttrib, instanceBuffer!!, stride, offset, gl.FLOAT)
        }
        return binders
    }

    fun checkBuffers() {
        checkIsNotReleased()

        if (instances != null && instanceBuffer != null && (instances.hasChanged || isNewlyCreated)) {
            instanceBuffer.setData(instances.dataF, instances.usage.glUsage)
            instances.hasChanged = false
        }

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

    private fun makeAttribBinders(
        shaderAttrs: Map<String, VertexLayout.VertexAttribute>,
        geomAttr: Attribute,
        buffer: BufferResource,
        stride: Int,
        offset: Int,
        type: Int
    ): List<AttributeBinder> {
        val binders = mutableListOf<AttributeBinder>()
        shaderAttrs[geomAttr.name]?.let { shaderAttr ->
            val (slots, size) = geomAttr.glAttribLayout
            for (i in 0 until slots) {
                val off = offset + size * i
                binders += AttributeBinder(buffer, size, stride, off, type, shaderAttr.location + i)
            }
        }
        return binders
    }

    private val Usage.glUsage: Int get() = when (this) {
        Usage.DYNAMIC -> gl.DYNAMIC_DRAW
        Usage.STATIC -> gl.STATIC_DRAW
    }

    private val Attribute.glAttribLayout: AttribLayout
        get() = when (type) {
        GlslType.FLOAT -> AttribLayout(1, 1)
        GlslType.VEC_2F -> AttribLayout(1, 2)
        GlslType.VEC_3F -> AttribLayout(1, 3)
        GlslType.VEC_4F -> AttribLayout(1, 4)
        GlslType.INT -> AttribLayout(1, 1)
        GlslType.VEC_2I -> AttribLayout(1, 2)
        GlslType.VEC_3I -> AttribLayout(1, 3)
        GlslType.VEC_4I -> AttribLayout(1, 4)
        GlslType.MAT_2F -> AttribLayout(2, 2)
        GlslType.MAT_3F -> AttribLayout(3, 3)
        GlslType.MAT_4F -> AttribLayout(4, 4)
    }

    private data class AttribLayout(val slots: Int, val size: Int)

    inner class AttributeBinder(
        val vbo: BufferResource,
        val elemSize: Int,
        val strideBytes: Int,
        val offset: Int,
        val type: Int,
        val loc: Int
    ) {
        val isIntAttribute: Boolean = type == gl.INT || type == gl.UNSIGNED_INT

        fun bindAttribute(target: Int) {
            vbo.bind()
            if (isIntAttribute) {
                gl.vertexAttribIPointer(target, elemSize, type, strideBytes, offset * 4)
            } else {
                gl.vertexAttribPointer(target, elemSize, type, false, strideBytes, offset * 4)
            }
        }
    }
}