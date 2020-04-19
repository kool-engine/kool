package de.fabmax.kool.drawqueue

import de.fabmax.kool.math.Mat4d
import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.scene.Mesh

class DrawCommand(val renderPass: RenderPass) {

    lateinit var mesh: Mesh
    var pipeline: Pipeline? = null

    val modelMat = Mat4f()
    val viewMat = Mat4f()
    val projMat = Mat4f()
    val mvpMat = Mat4f()

    private val mvpMatD = Mat4d()

    fun captureMatrices() {
        modelMat.set(mesh.modelMat)
        viewMat.set(renderPass.camera.view)
        projMat.set(renderPass.camera.proj)

        renderPass.camera.mvp.mul(mesh.modelMat, mvpMatD)
        mvpMat.set(mvpMatD)
    }
}