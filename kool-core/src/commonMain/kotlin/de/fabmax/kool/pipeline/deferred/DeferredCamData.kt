package de.fabmax.kool.pipeline.deferred

import de.fabmax.kool.KoolContext
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.KslShaderListener
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.pipeline.Uniform3f
import de.fabmax.kool.pipeline.Uniform4f
import de.fabmax.kool.pipeline.UniformMat4f
import de.fabmax.kool.pipeline.drawqueue.DrawCommand

fun KslProgram.deferredCameraData(): DeferredCamData {
    return (dataBlocks.find { it is DeferredCamData } as? DeferredCamData) ?: DeferredCamData(this)
}

class DeferredCamData(program: KslProgram) : KslDataBlock, KslShaderListener {

    override val name = "DeferredCamData"

    val position: KslUniformVector<KslTypeFloat3, KslTypeFloat1>
    val projMat: KslUniformMatrix<KslTypeMat4, KslTypeFloat4>
    val invViewMat: KslUniformMatrix<KslTypeMat4, KslTypeFloat4>
    val viewport: KslUniformVector<KslTypeFloat4, KslTypeFloat1>

    val camUbo = KslUniformBuffer("CameraUniforms", program, false).apply {
        projMat = uniformMat4("uProjMat")
        invViewMat = uniformMat4("uInvViewMat")
        viewport = uniformFloat4("uViewport")
        position = uniformFloat3("uCamPos")
    }

    private var uPosition: Uniform3f? = null
    private var uProjMat: UniformMat4f? = null
    private var uInvViewMat: UniformMat4f? = null
    private var uViewport: Uniform4f? = null

    init {
        program.shaderListeners += this
        program.dataBlocks += this
        program.uniformBuffers += camUbo
    }

    override fun onShaderCreated(shader: KslShader, pipeline: Pipeline, ctx: KoolContext) {
        uPosition = shader.uniforms["uCamPos"] as Uniform3f?
        uProjMat = shader.uniforms["uProjMat"] as UniformMat4f?
        uInvViewMat = shader.uniforms["uInvViewMat"] as UniformMat4f?
        uViewport = shader.uniforms["uViewport"] as Uniform4f?
    }

    override fun onUpdate(cmd: DrawCommand) {
        val q = cmd.queue
        val vp = q.renderPass.viewport
        uViewport?.value?.set(vp.x.toFloat(), vp.y.toFloat(), vp.width.toFloat(), vp.height.toFloat())

        val cam = q.renderPass.camera
        uPosition?.value?.set(cam.globalPos)
        uProjMat?.value?.set(q.projMat)
        uInvViewMat?.value?.set(q.invViewMatF)
    }
}