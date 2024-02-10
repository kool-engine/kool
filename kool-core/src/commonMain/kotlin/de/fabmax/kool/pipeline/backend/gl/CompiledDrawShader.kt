package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.pipeline.DrawPipeline
import de.fabmax.kool.pipeline.InputRate
import de.fabmax.kool.pipeline.PipelineBackend
import de.fabmax.kool.pipeline.VertexLayout
import de.fabmax.kool.pipeline.DrawCommand
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.geometry.PrimitiveType

class CompiledDrawShader(val pipeline: DrawPipeline, program: GlProgram, backend: RenderBackendGl) :
    CompiledShader(pipeline, program, backend),
    PipelineBackend
{
    private val attributes: Map<String, VertexLayout.VertexAttribute> = pipeline.vertexLayout.bindings
        .filter { it.inputRate == InputRate.VERTEX }
        .flatMap { it.vertexAttributes }
        .associateBy { it.attribute.name }
    private val instanceAttributes: Map<String, VertexLayout.VertexAttribute> = pipeline.vertexLayout.bindings
        .filter { it.inputRate == InputRate.INSTANCE }
        .flatMap { it.vertexAttributes }
        .associateBy { it.attribute.name }

    val mappedAttribLocations = pipeline.vertexLayout.getAttribLocations()
    private val attributeLocations: IntArray = attributes.values
        .flatMap { attr -> mappedAttribLocations[attr]!!.let { loc -> loc until loc + attr.locationSize } }
        .toIntArray()
    private val instanceAttributeLocations: IntArray = instanceAttributes.values
        .flatMap { attr -> mappedAttribLocations[attr]!!.let { loc -> loc until loc + attr.locationSize } }
        .toIntArray()

    private val floatAttrBinder: AttributeBinder?
    private val intAttrBinder: AttributeBinder?
    private val instanceAttrBinder: AttributeBinder?

    private val users = mutableSetOf<Int>()

    private var drawInfo = DrawInfo(pipeline.vertexLayout.primitiveType.glElemType, gl.UNSIGNED_INT, 0, false)

    init {
        val floatBinding = pipeline.vertexLayout.bindings
            .filter { it.inputRate == InputRate.VERTEX }
            .find { it.vertexAttributes.any { attr -> !attr.type.isInt } }
        val intBinding = pipeline.vertexLayout.bindings
            .filter { it.inputRate == InputRate.VERTEX }
            .find { it.vertexAttributes.any { attr -> attr.type.isInt } }
        val instanceBinding = pipeline.vertexLayout.bindings
            .find { it.inputRate == InputRate.INSTANCE }

        floatAttrBinder = floatBinding?.let { binding ->
            val attrs = binding.vertexAttributes.map { attr ->
                val location = mappedAttribLocations[attr]!!
                AttributeBinderItem(location, attr.type.channels, attr.bufferOffset / 4, gl.FLOAT)
            }
            AttributeBinder(attrs, binding.strideBytes)
        }
        intAttrBinder = intBinding?.let { binding ->
            val attrs = binding.vertexAttributes.map { attr ->
                val location = mappedAttribLocations[attr]!!
                AttributeBinderItem(location, attr.type.channels, attr.bufferOffset / 4, gl.INT)
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
    }

    fun disableVertexLayout() {
        for (i in attributeLocations.indices) {
            gl.disableVertexAttribArray(attributeLocations[i])
        }
        for (i in instanceAttributeLocations.indices) {
            gl.disableVertexAttribArray(instanceAttributeLocations[i])
        }
    }

    fun bindMesh(cmd: DrawCommand): DrawInfo {
        val pipeline = cmd.pipeline!!
        val geom = getOrCreateGpuGeometry(cmd)

        users.add(cmd.mesh.id)

        // update uniform values (camera + transform matrices, etc.)
        pipeline.update(cmd)

        // update geometry buffers (vertex + instance data)
        geom.checkBuffers()
        drawInfo.numIndices = geom.numIndices

        // bind uniform data
        val rp = cmd.queue.view.renderPass
        val viewData = cmd.queue.view.viewPipelineData.getPipelineData(pipeline)
        val meshData = cmd.mesh.meshPipelineData.getPipelineData(pipeline)
        drawInfo.isValid = bindUniforms(rp, viewData, meshData)

        // bind vertex data
        if (drawInfo.isValid) {
            geom.indexBuffer.bind()
            geom.dataBufferF?.let { floatAttrBinder?.bindAttributes(it) }
            geom.dataBufferI?.let { intAttrBinder?.bindAttributes(it) }
            geom.instanceBuffer?.let { instanceAttrBinder?.bindAttributes(it) }
        }

        return drawInfo
    }

    override fun removeUser(user: Any) {
        (user as? Mesh)?.let { users.remove(it.id) }
        if (users.isEmpty()) {
            release()
        }
    }

    override fun release() {
        if (!isReleased) {
            backend.shaderMgr.removeDrawShader(this)
            super.release()
        }
    }

    private fun getOrCreateGpuGeometry(cmd: DrawCommand): GpuGeometryGl {
        if (cmd.geometry.gpuGeometry == null) {
            cmd.geometry.gpuGeometry = GpuGeometryGl(cmd.geometry, cmd.mesh.instances, backend, BufferCreationInfo(cmd))
        }
        return cmd.geometry.gpuGeometry as GpuGeometryGl
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
        fun bindAttributes(buffer: BufferResource) {
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