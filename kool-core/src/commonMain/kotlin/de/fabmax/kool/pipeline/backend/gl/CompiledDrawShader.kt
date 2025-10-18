package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.scene.MeshInstanceList
import de.fabmax.kool.scene.geometry.PrimitiveType

class CompiledDrawShader(val pipeline: DrawPipeline, program: GlProgram, backend: RenderBackendGl) :
    CompiledShader(pipeline, program, backend),
    PipelineBackend
{
    private val attributes: Map<String, VertexLayout.VertexAttribute> = pipeline.vertexLayout.bindings
        .filter { it.inputRate == InputRate.VERTEX }
        .flatMap { it.vertexAttributes }
        .associateBy { it.name }
    private val instanceAttributes: Map<String, VertexLayout.VertexAttribute> = pipeline.vertexLayout.bindings
        .filter { it.inputRate == InputRate.INSTANCE }
        .flatMap { it.vertexAttributes }
        .associateBy { it.name }

    val mappedAttribLocations = pipeline.vertexLayout.getAttribLocations()
    private val attributeLocations: IntArray = attributes.values
        .flatMap { attr -> mappedAttribLocations[attr]!!.let { loc -> loc until loc + attr.locationSize } }
        .toIntArray()
    private val instanceAttributeLocations: IntArray = instanceAttributes.values
        .flatMap { attr -> mappedAttribLocations[attr]!!.let { loc -> loc until loc + attr.locationSize } }
        .toIntArray()

    private var vao: GlVertexArrayObject? = null
    private val vertexAttrBinder: AttributeBinder?
    private val instanceAttrBinder: AttributeBinder?

    private var drawInfo = DrawInfo(pipeline.vertexLayout.primitiveType.glElemType, gl.UNSIGNED_INT, 0, false)

    init {
        val vertexBinding = pipeline.vertexLayout.bindings
            .find { it.inputRate == InputRate.VERTEX }
        val instanceBinding = pipeline.vertexLayout.bindings
            .find { it.inputRate == InputRate.INSTANCE }

        vertexAttrBinder = vertexBinding?.let { binding ->
            val attrs = binding.vertexAttributes.map { attr ->
                val location = mappedAttribLocations[attr]!!
                AttributeBinderItem(location, attr.type.channels, attr.bufferOffset / 4, gl.FLOAT)
            }
            AttributeBinder(attrs, binding.strideBytes)
        }
        instanceAttrBinder = instanceBinding?.let { binding ->
            val attrs = binding.vertexAttributes.flatMap { attr ->
                val type = if (attr.type.isInt) gl.INT else gl.FLOAT
                val location = mappedAttribLocations[attr]!!
                val slots = attr.locationSize
                // instance attributes can contain matrices, which occupy ont attribute slot per column
                (0 until slots).map { i ->
                    AttributeBinderItem(location + i, attr.type.channels, attr.bufferOffset / 4 + i * attr.type.channels, type)
                }
            }
            AttributeBinder(attrs, binding.strideBytes)
        }
    }

    fun enableVertexLayout() {
        val vao = this.vao ?: createVao()
        gl.bindVertexArray(vao)
    }

    fun disableVertexLayout() {
        gl.bindVertexArray(gl.NULL_VAO)
    }

    private fun createVao(): GlVertexArrayObject {
        val vao = gl.createVertexArray().also { this.vao = it }
        gl.bindVertexArray(vao)
        for (i in attributeLocations.indices) {
            val location = attributeLocations[i]
            gl.enableVertexAttribArray(location)
            gl.vertexAttribDivisor(location, 0)
        }
        for (i in instanceAttributeLocations.indices) {
            val location = instanceAttributeLocations[i]
            gl.enableVertexAttribArray(location)
            gl.vertexAttribDivisor(location, 1)
        }
        gl.bindVertexArray(gl.NULL_VAO)
        return vao
    }

    fun bindMesh(cmd: DrawCommand): DrawInfo {
        drawInfo.numIndices = 0

        // bind uniform data
        val rp = cmd.queue.view.renderPass
        val viewData = cmd.queue.view.viewPipelineData.getPipelineData(pipeline)
        val meshData = cmd.mesh.meshPipelineData.getPipelineData(pipeline)
        drawInfo.isValid = bindUniforms(rp, viewData, meshData)

        // bind vertex data
        if (drawInfo.isValid) {
            val geom = getOrCreateGpuGeometry(cmd)
            geom.indexBuffer.bind()
            geom.dataBuffer?.let { vertexAttrBinder?.bindAttributes(it) }
            geom.checkBuffers()
            drawInfo.numIndices = geom.numIndices

            cmd.instanceData?.let { insts ->
                val gpuInsts = insts.getOrCreateGpuInstances(cmd)
                gpuInsts.checkBuffers()
                gpuInsts.instanceBuffer.let { instanceAttrBinder?.bindAttributes(it) }
            }
        }

        return drawInfo
    }

    override fun doRelease() {
        super.doRelease()
        backend.shaderMgr.removeDrawShader(this)
        vao?.let { gl.deleteVertexArray(it) }
    }

    private fun getOrCreateGpuGeometry(cmd: DrawCommand): GpuGeometryGl {
        if (cmd.vertexData.gpuGeometry == null) {
            cmd.vertexData.gpuGeometry = GpuGeometryGl(cmd.vertexData, backend, BufferCreationInfo(cmd))
        }
        return cmd.vertexData.gpuGeometry as GpuGeometryGl
    }

    private fun MeshInstanceList<*>.getOrCreateGpuInstances(cmd: DrawCommand): GpuInstancesGl {
        if (gpuInstances == null) {
            gpuInstances = GpuInstancesGl(this, backend, BufferCreationInfo(cmd))
        }
        return gpuInstances as GpuInstancesGl
    }

    private val PrimitiveType.glElemType: Int get() = when (this) {
        PrimitiveType.LINES -> gl.LINES
        PrimitiveType.POINTS -> gl.POINTS
        PrimitiveType.TRIANGLES -> gl.TRIANGLES
        PrimitiveType.TRIANGLE_STRIP -> gl.TRIANGLE_STRIP
    }

    class DrawInfo(val primitiveType: Int, val indexType: Int, var numIndices: Int, var isValid: Boolean)

    inner class AttributeBinder(
        val items: List<AttributeBinderItem>,
        val strideBytes: Int,
    ) {
        fun bindAttributes(buffer: GpuBufferGl) {
            buffer.bind()
            for (i in items.indices) {
                val item = items[i]
                if (item.isIntType) {
                    gl.vertexAttribIPointer(item.location, item.elemSize, item.type, strideBytes, item.offset * 4)
                } else {
                    gl.vertexAttribPointer(item.location, item.elemSize, item.type, false, strideBytes, item.offset * 4)
                }
            }
        }
    }

    private val AttributeBinderItem.isIntType: Boolean get() = type == gl.INT || type == gl.UNSIGNED_INT

    data class AttributeBinderItem(val location: Int, val elemSize: Int, val offset: Int, val type: Int)
}