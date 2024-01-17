package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.pipeline.Attribute
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

    private val name = creationInfo.bufferName

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

    override fun toString(): String {
        return "GpuGeometryGl(name=$name, geometry.name=${geometry.name})"
    }

    override fun release() {
        indexBuffer.release()
        dataBufferF?.release()
        dataBufferI?.release()
        instanceBuffer?.release()
        super.release()
    }

    fun createShaderVertexAttributeBinders(
        shaderAttributes: Map<String, VertexLayout.VertexAttribute>,
        attribLocations: Map<VertexLayout.VertexAttribute, Int>
    ): List<AttributeBinder> {
        val binders = mutableListOf<AttributeBinder>()
        geometry.vertexAttributes
            .filter { !it.type.isInt }
            .forEach { geomAttr ->
                val stride = geometry.byteStrideF
                val offset = geometry.attributeByteOffsets[geomAttr]!! / 4
                binders += makeAttribBinders(shaderAttributes, attribLocations, geomAttr, dataBufferF!!, stride, offset, gl.FLOAT)
            }
        geometry.vertexAttributes
            .filter { it.type.isInt }
            .forEach { geomAttr ->
                val stride = geometry.byteStrideI
                val offset = geometry.attributeByteOffsets[geomAttr]!! / 4
                binders += makeAttribBinders(shaderAttributes, attribLocations, geomAttr, dataBufferI!!, stride, offset, gl.INT)
            }
        return binders
    }

    fun createShaderInstanceAttributeBinders(
        shaderAttributes: Map<String, VertexLayout.VertexAttribute>,
        attribLocations: Map<VertexLayout.VertexAttribute, Int>
    ): List<AttributeBinder> {
        instances ?: return emptyList()

        val binders = mutableListOf<AttributeBinder>()
        for (instanceAttrib in instances.instanceAttributes) {
            val stride = instances.strideBytesF
            val offset = instances.attributeOffsets[instanceAttrib]!! / 4
            binders += makeAttribBinders(shaderAttributes, attribLocations, instanceAttrib, instanceBuffer!!, stride, offset, gl.FLOAT)
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
        attribLocations: Map<VertexLayout.VertexAttribute, Int>,
        geomAttr: Attribute,
        buffer: BufferResource,
        stride: Int,
        offset: Int,
        type: Int
    ): List<AttributeBinder> {
        val binders = mutableListOf<AttributeBinder>()
        shaderAttrs[geomAttr.name]?.let { shaderAttr ->
            attribLocations[shaderAttr]!!.let { location ->
                val slots = shaderAttr.locationSize
                val elemSize = shaderAttr.type.channels
                for (i in 0 until slots) {
                    val off = offset + elemSize * i
                    binders += AttributeBinder(buffer, elemSize, stride, off, type, location + i)
                }
            }
        }
        return binders
    }

    private val Usage.glUsage: Int get() = when (this) {
        Usage.DYNAMIC -> gl.DYNAMIC_DRAW
        Usage.STATIC -> gl.STATIC_DRAW
    }

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