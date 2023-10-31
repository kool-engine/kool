package de.fabmax.kool.pipeline.drawqueue

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableMat4d
import de.fabmax.kool.math.MutableMat4f
import de.fabmax.kool.math.set
import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.geometry.IndexedVertexList

class DrawCommand(val renderPass: RenderPass, var mesh: Mesh) {

    var geometry: IndexedVertexList = mesh.geometry
    var pipeline: Pipeline? = null

    val modelMat = MutableMat4f()
    val viewMat = MutableMat4f()
    val projMat = MutableMat4f()
    val mvpMat = MutableMat4f()

    private val mvpMatD = MutableMat4d()

    fun setup(mesh: Mesh, ctx: KoolContext) {
        this.mesh = mesh
        geometry = mesh.geometry
        pipeline = mesh.getPipeline(ctx)
        captureMatrices()
    }

    fun captureMatrices() {
        modelMat.set(mesh.modelMat)
        viewMat.set(renderPass.camera.view)
        projMat.set(renderPass.camera.proj)

        renderPass.camera.viewProj.mul(mesh.modelMat, mvpMatD)
        mvpMat.set(mvpMatD)
    }
}