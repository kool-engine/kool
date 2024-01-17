package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.drawqueue.DrawCommand
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshInstanceList
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.scene.geometry.PrimitiveType
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.logE

class CompiledDrawShader(val pipeline: DrawPipeline, program: GlProgram, backend: RenderBackendGl) :
    CompiledShader(pipeline, program, backend),
    DrawPipelineBackend
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

    private val meshInstances = mutableMapOf<Int, ShaderMeshInstance>()

    init {
        pipeline.pipelineBackend = this
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

    fun bindMesh(cmd: DrawCommand): ShaderMeshInstance? {
        val inst = meshInstances.getOrPut(cmd.mesh.id) { ShaderMeshInstance(cmd) }
        return if (inst.bindInstance(cmd)) { inst } else { null }
    }

    override fun release() {
        if (!isReleased) {
            meshInstances.values.forEach { it.release() }
            meshInstances.clear()
            backend.shaderMgr.removeDrawShader(this)
            super.release()
        }
    }

    override fun releaseMeshInstance(mesh: Mesh) {
        meshInstances.remove(mesh.id)?.release()
        if (meshInstances.isEmpty()) {
            release()
        }
    }

    fun isEmpty(): Boolean = meshInstances.isEmpty()

    inner class ShaderMeshInstance(cmd: DrawCommand) : BaseReleasable() {
        private val mesh: Mesh = cmd.mesh
        private var geometry: IndexedVertexList = cmd.geometry
        private val instances: MeshInstanceList? get() = mesh.instances

        private val attributeBinders = mutableListOf<GpuGeometryGl.AttributeBinder>()
        private val instanceAttribBinders = mutableListOf<GpuGeometryGl.AttributeBinder>()
        private var gpuGeometry: GpuGeometryGl? = null

        private val mappedMeshGroup = mapBindGroup(BindGroupScope.MESH)

        val primitiveType = pipeline.vertexLayout.primitiveType.glElemType
        val indexType = gl.UNSIGNED_INT
        val numIndices: Int get() = gpuGeometry?.numIndices ?: 0

        init {
            pipelineInfo.numInstances++
            createBuffers(cmd)
        }

        private fun createBuffers(cmd: DrawCommand) {
            val creationInfo = BufferCreationInfo(cmd)

            var geom = geometry.gpuGeometry as? GpuGeometryGl
            if (geom == null || geom.isReleased) {
                if (geom?.isReleased == true) {
                    logE { "Mesh geometry is already released: ${pipeline.name}" }
                }
                geom = GpuGeometryGl(geometry, instances, backend, creationInfo)
                geometry.gpuGeometry = geom
            }
            gpuGeometry = geom

            checkMeshAttributes()
            attributeBinders += geom.createShaderVertexAttributeBinders(attributes, mappedAttribLocations)
            instanceAttribBinders += geom.createShaderInstanceAttributeBinders(instanceAttributes, mappedAttribLocations)

            mappedMeshGroup?.createBuffers(cmd.queue.renderPass)
            createBindGroups(cmd.queue.renderPass)
        }

        private fun checkMeshAttributes() {
            val checkVertexAttributes = attributes.keys - geometry.vertexAttributes.map { it.name }.toSet()
            check(checkVertexAttributes.isEmpty()) {
                "Mesh ${mesh.name} misses vertex attributes $checkVertexAttributes required for pipeline ${pipeline.name}"
            }
            mesh.instances?.let { instances ->
                val checkInstanceAttributes = instanceAttributes.keys - instances.instanceAttributes.map { it.name }.toSet()
                check(checkInstanceAttributes.isEmpty()) {
                    "Mesh ${mesh.name} misses instance attributes $checkInstanceAttributes required for pipeline ${pipeline.name}"
                }
            }
        }

        private fun bindUniforms(cmd: DrawCommand): Boolean {
            return bindUniforms(cmd.queue.view) &&
                mappedMeshGroup?.bindUniforms(mesh.meshPipelineData.getPipelineData(pipeline)) != false
        }

        private fun releaseGeometryBuffers() {
            attributeBinders.clear()
            instanceAttribBinders.clear()

            gpuGeometry?.let {
                if (!it.isReleased) {
                    it.release()
                }
            }
            gpuGeometry = null
        }

        fun bindInstance(drawCmd: DrawCommand): Boolean {
            if (geometry !== drawCmd.geometry) {
                geometry = drawCmd.geometry
                releaseGeometryBuffers()
                createBuffers(drawCmd)
            }
            val geom = gpuGeometry ?: return false

            // call onUpdate callbacks
            for (i in pipeline.onUpdate.indices) {
                pipeline.onUpdate[i].invoke(drawCmd)
            }

            // update geometry buffers (vertex + instance data)
            geom.checkBuffers()

            val uniformsValid = bindUniforms(drawCmd)
            if (uniformsValid) {
                // bind vertex data
                geom.indexBuffer.bind()
                attributeBinders.forEach { it.bindAttribute(it.loc) }
                instanceAttribBinders.forEach { it.bindAttribute(it.loc) }
            }
            return uniformsValid
        }

        override fun release() {
            super.release()
            releaseGeometryBuffers()
            mappedMeshGroup?.releaseBuffers()
            pipelineInfo.numInstances--
        }
    }

    private val PrimitiveType.glElemType: Int get() = when (this) {
        PrimitiveType.LINES -> gl.LINES
        PrimitiveType.POINTS -> gl.POINTS
        PrimitiveType.TRIANGLES -> gl.TRIANGLES
    }
}