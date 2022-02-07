package de.fabmax.kool.pipeline.drawqueue

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Mat4d
import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.geometry.IndexedVertexList

class DrawCommand(val renderPass: RenderPass, var mesh: Mesh) {

    var geometry: IndexedVertexList = mesh.geometry
    var pipeline: Pipeline? = null

    val modelMat = Mat4f()
    val viewMat = Mat4f()
    val projMat = Mat4f()
    val mvpMat = Mat4f()

    private val mvpMatD = Mat4d()

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