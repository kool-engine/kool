package de.fabmax.kool.pipeline.drawqueue

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Mat4d
import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.geometry.IndexedVertexList

class DrawCommand(val queue: DrawQueue, mesh: Mesh) {

    var mesh: Mesh = mesh
        private set

    var geometry: IndexedVertexList = mesh.geometry
    var pipeline: Pipeline? = null

    /**
     * Single precision model matrix of this command's [mesh].
     */
    val modelMatF: Mat4f get() = mesh.modelMatF

    /**
     * Double precision model matrix of this command's [mesh].
     */
    val modelMatD: Mat4d get() = mesh.modelMatD

    fun setup(mesh: Mesh, ctx: KoolContext) {
        this.mesh = mesh
        geometry = mesh.geometry
        pipeline = mesh.getPipeline(ctx)
    }
}