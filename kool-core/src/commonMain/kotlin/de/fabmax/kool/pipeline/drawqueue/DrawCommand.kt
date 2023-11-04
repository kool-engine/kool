package de.fabmax.kool.pipeline.drawqueue

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableMat4d
import de.fabmax.kool.math.MutableMat4f
import de.fabmax.kool.math.set
import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.geometry.IndexedVertexList

class DrawCommand(val queue: DrawQueue, mesh: Mesh) {

    var mesh: Mesh = mesh
        private set

    var geometry: IndexedVertexList = mesh.geometry
    var pipeline: Pipeline? = null

    val isDoublePrecision: Boolean get() = queue.isDoublePrecision

    /**
     * Single precision model matrix captured from this command's [mesh]; only valid if [isDoublePrecision] is false.
     * @see [modelMatD]
     */
    val modelMatF = MutableMat4f()

    /**
     * Double precision model matrix captured from this command's [mesh]; only valid if [isDoublePrecision] is true.
     * @see [modelMatF]
     */
    val modelMatD = MutableMat4d()

    fun setup(mesh: Mesh, ctx: KoolContext) {
        this.mesh = mesh
        geometry = mesh.geometry
        pipeline = mesh.getPipeline(ctx)

        if (isDoublePrecision) {
            modelMatD.set(mesh.modelMat)
        } else {
            modelMatF.set(mesh.modelMat)
        }
    }
}