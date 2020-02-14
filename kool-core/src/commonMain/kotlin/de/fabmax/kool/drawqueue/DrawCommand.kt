package de.fabmax.kool.drawqueue

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.scene.Mesh

class DrawCommand {

    lateinit var mesh: Mesh
    var pipeline: Pipeline? = null

    val modelMat = Mat4f()
    val viewMat = Mat4f()
    val projMat = Mat4f()
    val mvpMat = Mat4f()

    var indexBuffer = 0L
    var vertexBuffer = 0L

    fun captureMvp(ctx: KoolContext) {
        modelMat.set(ctx.mvpState.modelMatrix)
        viewMat.set(ctx.mvpState.viewMatrix)
        projMat.set(ctx.mvpState.projMatrix)
        mvpMat.set(ctx.mvpState.mvpMatrix)
    }
}