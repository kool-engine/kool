package de.fabmax.kool.pipeline.deferred

import de.fabmax.kool.modules.ksl.KslShaderListener
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.drawqueue.DrawCommand

fun KslProgram.deferredCameraData(): DeferredCamData {
    return (dataBlocks.find { it is DeferredCamData } as? DeferredCamData) ?: DeferredCamData(this)
}

class DeferredCamData(program: KslProgram) : KslDataBlock, KslShaderListener {

    override val name = "DeferredCamData"

    val position: KslUniformVector<KslFloat3, KslFloat1>
    val projMat: KslUniformMatrix<KslMat4, KslFloat4>
    val invViewMat: KslUniformMatrix<KslMat4, KslFloat4>
    val viewport: KslUniformVector<KslFloat4, KslFloat1>

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

    override fun onShaderCreated(shader: ShaderBase<*>, pipeline: PipelineBase) {
        uPosition = shader.uniforms["uCamPos"] as Uniform3f?
        uProjMat = shader.uniforms["uProjMat"] as UniformMat4f?
        uInvViewMat = shader.uniforms["uInvViewMat"] as UniformMat4f?
        uViewport = shader.uniforms["uViewport"] as Uniform4f?
    }

    override fun onUpdate(cmd: DrawCommand) {
        val q = cmd.queue
        val vp = q.view.viewport
        uViewport?.value?.set(vp.x.toFloat(), vp.y.toFloat(), vp.width.toFloat(), vp.height.toFloat())

        val cam = q.view.camera
        uPosition?.value?.set(cam.globalPos)
        uProjMat?.value?.set(q.projMat)
        uInvViewMat?.value?.set(q.invViewMatF)
    }
}