package de.fabmax.kool.pipeline

import de.fabmax.kool.math.Mat4d
import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshInstanceList
import de.fabmax.kool.scene.geometry.IndexedVertexList

class DrawCommand(val queue: DrawQueue, var pipeline: DrawPipeline) {
    var drawGroupId = 0
        private set

    lateinit var mesh: Mesh<*>
    lateinit var vertexData: IndexedVertexList<*>
    var instanceData: MeshInstanceList<*>? = null

    var isActive = true

    /**
     * Single precision model matrix of this command's [mesh].
     */
    val modelMatF: Mat4f get() = mesh.modelMatF

    /**
     * Double precision model matrix of this command's [mesh].
     */
    val modelMatD: Mat4d get() = mesh.modelMatD

    fun setup(mesh: Mesh<*>, vertexData: IndexedVertexList<*>, instanceData: MeshInstanceList<*>?, pipeline: DrawPipeline, drawGroupId: Int) {
        this.mesh = mesh
        this.pipeline = pipeline
        this.drawGroupId = drawGroupId
        this@DrawCommand.vertexData = vertexData
        this@DrawCommand.instanceData = instanceData
        isActive = true
    }

    fun captureData() {
        mesh.captureBuffer()
        pipeline.captureBuffer()
        pipeline.addUser(mesh)
    }

    fun updatePipelineData() {
        pipeline.updatePipelineData(this)
    }
}